package com.demo.springbootdemo.services;

import com.demo.springbootdemo.configuration.JwtUtil;
import com.demo.springbootdemo.controller.*;
import com.demo.springbootdemo.entity.*;
import com.demo.springbootdemo.model.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/public")
public class PublicService {

    private final JwtUtil jwtUtil;

    @Autowired
    private EventController eventController;

    @Autowired
    private TeamController teamController;

    public PublicService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private UserController userController;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private CloudinaryController cloudinaryController;

    @Autowired
    private CompanyController companyController;

    @Autowired
    private DocumentController documentController;

    @Autowired
    private HolidayController holidayController;

    @Autowired
    private EmailController emailController;

    @PostMapping("/upload-profile")
    public ResponseEntity<ApiResponse<User>> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                                  HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<User> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userController.getUserByEmail(authentication.getPrincipal().toString());
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                try {
                    Map<String, String> uploadResponse = cloudinaryController.uploadProfilePicture(file);
                    String pictureUrl = uploadResponse.get("url");
                    String pictureId = uploadResponse.get("publicId");
                    if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty())
                        cloudinaryController.deleteFile(user.getProfilePicturePublicId());
                    user.setProfilePictureUrl(pictureUrl);
                    user.setProfilePicturePublicId(pictureId);
                    user = userController.setUser(user);
                    response.setStatus(HttpStatus.OK);
                    response.setMessageLabel("upload_profile_picture_success");
                    response.setData(user);
                    response.setSuccess(true);

                } catch (Exception e) {
                    response.setData(null);
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    response.setSuccess(false);
                    response.setMessageLabel("upload_profile_picture_failed");
                }
            }
        }
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/upload-document")
    public ResponseEntity<ApiResponse<Document>> uploadDocument(@RequestParam("to") String toEmail, @RequestParam("description") String description, @RequestParam("file") MultipartFile file,
                                                                HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<Document> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            if (toEmail.isEmpty()) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("manage_document_upload_document_missing_to_user_error_message");
            } else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User userFrom = userController.getUserByEmail(authentication.getPrincipal().toString());
                User userTo = userController.getUserByEmail(toEmail);
                if (userTo == null || userFrom == null) {
                    response.setData(null);
                    response.setStatus(HttpStatus.UNAUTHORIZED);
                    response.setSuccess(false);
                    response.setMessageLabel("auth_profile_expired_error_message");
                    response.setDoLogout(true);
                } else {
                    try {
                        Map<String, String> uploadResponse = cloudinaryController.uploadDocument(file);
                        String documentUrl = uploadResponse.get("url");
                        String documentId = uploadResponse.get("publicId");
                        String originalFilename = file.getOriginalFilename();
                        Document document = new Document();
                        document.setAt(new Date().getTime());
                        document.setFrom(userFrom);
                        document.setTo(userTo);
                        document.setUrl(documentUrl);
                        document.setType(FilenameUtils.getExtension(originalFilename));
                        document.setDescription(description);
                        document.setName(FilenameUtils.removeExtension(originalFilename));
                        document.setSize(Math.round(file.getSize() / 1024.0) + "KB");
                        document.setCloudId(documentId);

                        document = documentController.saveDocument(document);

                        userTo.setDocumentsTo(document);
                        userFrom.setDocumentsFrom(document);
                        userController.save(userFrom);
                        userController.save(userTo);

                        documentController.sendNotification(document, userFrom, userTo);

                        response.setStatus(HttpStatus.OK);
                        response.setMessageLabel("manage_document_upload_document_done");
                        response.setData(document);
                        response.setSuccess(true);

                    } catch (Exception e) {
                        response.setData(null);
                        response.setStatus(HttpStatus.BAD_REQUEST);
                        response.setSuccess(false);
                        response.setMessageLabel("manage_document_upload_document_failed");
                    }
                }
            }
        }
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(@RequestParam("email") String email,
                                                                               @RequestParam("all") boolean isAll,
                                                                               HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        ApiResponse<List<NotificationDTO>> response = new ApiResponse<>();
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(email);
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                List<Notification> notifications = isAll ? notificationController.getAllNotificationsByUserTo(user) :
                        notificationController.getLimitedNotificationsByUserTo(user);
                List<NotificationDTO> dtoList = new ArrayList<>();
                for (Notification notification : notifications) {
                    NotificationDTO dto = new NotificationDTO();
                    dto.setId(notification.getId());
                    dto.setAt(notification.getAt());
                    dto.setRead(notification.isRead());
                    dto.setMessageLabel(notification.getMessageLabel());
                    dto.setTitleLabel(notification.getTitleLabel());
                    dto.setFromName(notification.getFrom().getFirstname() + " " + notification.getFrom().getLastname());
                    dto.setFromId(notification.getFrom().getEmail());
                    dto.setFromProfilePictureUrl(notification.getFrom().getProfilePictureUrl());
                    dtoList.add(dto);
                }
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setData(dtoList);
                response.setSuccess(true);
            }
        }
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping(path = "/setnotificationsstatus")
    public ApiResponse<?> setNotificationsReadStatus(@RequestBody List<NotificationDTO> notificationsDTO) {
        ApiResponse<?> response = new ApiResponse<>();
        for (NotificationDTO notificationDTO : notificationsDTO) {
            Notification notification = notificationController.getNotificationsById(notificationDTO.getId());
            if (notification == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setShowToast(false);
                break;
            } else {
                notification.setRead(true);
                notificationController.saveNotification(notification);
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            }
        }
        return response;
    }

    @GetMapping(path = "/getnotificationdetails")
    public ApiResponse<Notification> getNotificationDetails(@RequestParam("id") Long id) {
        ApiResponse<Notification> response = new ApiResponse<>();
        Notification notification = notificationController.getNotificationsById(id);
        if (notification == null) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
        } else {
            response.setData(notification);
            response.setStatus(HttpStatus.OK);
            response.setShowToast(false);
            response.setSuccess(true);
        }
        return response;
    }

    @GetMapping(path = "/checkCurrentPassword")
    public ApiResponse<Boolean> checkCurrentPassword(@RequestParam("password") String password,
                                                     @RequestParam("email") String email) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        User user = userController.login(email, password);
        if (user == null) {
            response.setStatus(HttpStatus.OK);
            response.setShowToast(false);
            response.setSuccess(false);
        } else {
            response.setStatus(HttpStatus.OK);
            response.setShowToast(false);
            response.setSuccess(true);
        }
        return response;
    }

    @GetMapping(path = "/teammembers")
    public ApiResponse<List<TeamDetailsResponse>> getTeamMembers(@RequestParam("id") String userEmail, HttpServletRequest request) {
        ApiResponse<List<TeamDetailsResponse>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(userEmail);
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                List<TeamDetailsResponse> TeamDetailsResponseList = new ArrayList<>();
                List<Team> teams = new ArrayList<>();
                if (user.getRole().equals(Role.MANAGER)) {
                    teams = teamController.getTeamByManager(user);
                } else if (user.getRole().equals(Role.ADMIN)) {
                    Company company = companyController.getCompanyById(user.getCompany().getId());
                    List<User> admins = userController.getUsersByRole(Role.ADMIN);
                    Team adminsTeam = new Team();
                    adminsTeam.setName(company.getName());
                    adminsTeam.setManager(company.getCompanyCreator());
                    adminsTeam.getMembers().addAll(admins);
                    adminsTeam.setCompany(company);
                    teams.add(adminsTeam);
                } else {
                    teams.add(user.getTeam());
                }
                teams.forEach(team -> {
                    TeamDetailsResponse teamDetailsResponseList = new TeamDetailsResponse();
                    teamDetailsResponseList.setTeam(team);
                    teamDetailsResponseList.setMembers(team.getMembers());
                    teamDetailsResponseList.setManager(team.getManager());
                    TeamDetailsResponseList.add(teamDetailsResponseList);
                });
                response.setData(TeamDetailsResponseList);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }
        }
        return response;
    }

    @PostMapping(path = "/editprofile")
    public ApiResponse<User> editProfile(@RequestBody EditProfileRequest profileRequest, HttpServletRequest request) {
        ApiResponse<User> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(profileRequest.getEmail());
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                user.setAddress(profileRequest.getAddress());
                user.setCity(profileRequest.getCity());
                user.setCountry(profileRequest.getCountry());
                user.setDateOfBirth(profileRequest.getDateOfBirth().getTime());
                user.setDegree(profileRequest.getDegree());
                user.setFirstname(profileRequest.getFirstname());
                user.setLastname(profileRequest.getLastname());
                user.setPostCode(profileRequest.getPostCode());
                user.setTitle(profileRequest.getTitle());
                response.setData(userController.setUser(user));
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setMessageLabel("manage_profile_edit_profile_done");
            }
        }
        return response;
    }

    @GetMapping(path = "/getdocumentto")
    public ApiResponse<List<Document>> getDocumentTo(@RequestParam("email") String email, HttpServletRequest request) {
        ApiResponse<List<Document>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(email);
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                List<Document> documents = documentController.findByTo(user);
                response.setData(documents);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }
        }
        return response;
    }

    @GetMapping(path = "/getdocumentfrom")
    public ApiResponse<List<Document>> getDocumentFrom(@RequestParam("email") String email, HttpServletRequest request) {
        ApiResponse<List<Document>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(email);
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                List<Document> documents = documentController.findByFrom(user);
                response.setData(documents);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }
        }
        return response;
    }

    @GetMapping(path = "/gettolistusers")
    public ApiResponse<List<User>> getToListUsers(@RequestParam("email") String email, HttpServletRequest request) {
        ApiResponse<List<User>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(email);
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                List<User> result = new ArrayList<>();
                List<User> companyMember = user.getCompany().getMembers();
                if (user.getRole().equals(Role.ADMIN)) {
                    result.addAll(companyMember);
                } else if (user.getRole().equals(Role.MANAGER)) {
                    companyMember.forEach(member -> {
                        if (member.getRole().equals(Role.MANAGER)) {
                            result.add(member);
                        }
                    });
                    user.getTeams().forEach(team -> {
                        result.addAll(team.getMembers());
                    });
                } else {
                    if (user.getTeam() != null) {
                        result.add(user.getTeam().getManager());
                        result.addAll(user.getTeam().getMembers());
                    } else {
                        result.add(user.getCompany().getCompanyCreator());
                    }
                }
                result.remove(user);
                response.setData(result);
                response.setStatus(HttpStatus.OK);
                response.setSuccess(true);
                response.setShowToast(false);
            }
        }
        return response;
    }

    @PutMapping(path = "/editdocument")
    public ApiResponse<Document> editDocument(@RequestBody() DocumentDTO documentDTO, HttpServletRequest request) {
        ApiResponse<Document> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {

            Document document = documentController.findById(documentDTO.getDocumentId());
            document.setName(documentDTO.getName());
            document.setDescription(documentDTO.getDescription());
            User userTo = userController.getUserByEmail(documentDTO.getToUserEmail());
            document.setTo(userTo);
            document = documentController.saveDocument(document);
            documentController.sendEditDocumentNotification(document, document.getFrom(), userTo);
            response.setData(document);
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setMessageLabel("manage_document_edit_document_done");
        }
        return response;
    }

    @DeleteMapping(path = "/deletedocument")
    public ApiResponse<Document> deleteDocument(@RequestParam("id") Long idDocument, HttpServletRequest request) {
        ApiResponse<Document> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Document document = documentController.findById(idDocument);
            if (document == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                try {
                    cloudinaryController.deleteDocumentFile(document.getCloudId());
                    documentController.deleteDocument(document);
                } catch (IOException e) {
                    response.setData(null);
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setSuccess(false);
                    response.setMessageLabel("manage_document_delete_document_could_error");
                    documentController.deleteDocument(document);
                }
            }
            response.setData(document);
            response.setStatus(HttpStatus.OK);
            response.setSuccess(true);
            response.setMessageLabel("manage_document_edit_document_done");
        }
        return response;
    }

    @PutMapping(path = "/holidayrequest")
    public ApiResponse<List<Holiday>> requestHoliday(@RequestBody List<BookHolidayDTO> holidayDTOList, HttpServletRequest request) {
        ApiResponse<List<Holiday>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            String email = jwtUtil.extractUsername(token);
            User user = userController.getUserByEmail(email);
            float remainAnnualSold = user.getHolidaySold();
            float remainSicknessLeaverSold = user.getSicknessLeaverSold();
            for (BookHolidayDTO holidayDTO : holidayDTOList) {
                if (holidayDTO.getType().equals(HolidayType.ANNUAL_LEAVER))
                    remainAnnualSold = remainAnnualSold - holidayDTO.getCountedDays();
                else if (holidayDTO.getType().equals(HolidayType.SICKNESS_LEAVER))
                    remainSicknessLeaverSold = remainSicknessLeaverSold - holidayDTO.getCountedDays();
            }
            if (remainAnnualSold >= 0 && remainSicknessLeaverSold >= 0) {
                user.setHolidaySold(remainAnnualSold);
                user.setSicknessLeaverSold(remainSicknessLeaverSold);
                List<Holiday> result = new ArrayList<>();
                holidayDTOList.forEach(holidayDTO -> {
                    Holiday holiday = new Holiday();
                    holiday.setType(holidayDTO.getType());
                    holiday.setFrom(holidayDTO.getFrom());
                    holiday.setTo(holidayDTO.getTo());
                    holiday.setStatus(holidayDTO.getStatus());
                    holiday.setAt(new Date().getTime());
                    holiday.setUser(user);
                    result.add(holidayController.saveHoliday(holiday));
                    holidayController.HolidayRequestNotifyManager(holiday.getUser());
                });
                response.setData(result);
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            }
        }
        return response;
    }

    @GetMapping(path = "/getholidays")
    public ApiResponse<List<Holiday>> getHolidays(@RequestParam("id") String userEmail, HttpServletRequest request) {
        ApiResponse<List<Holiday>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(userEmail);
            if (user == null) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                response.setData(holidayController.getHolidaysListByUser(user));
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            }

        }
        return response;
    }

    @GetMapping(path = "/getholidaysrequests")
    public ApiResponse<List<Holiday>> getHolidaysRequests(@RequestParam("id") String userEmail, HttpServletRequest request) {
        ApiResponse<List<Holiday>> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            User user = userController.getUserByEmail(userEmail);
            if (user == null || user.getRole().equals(Role.EMPLOYEE)) {
                response.setData(null);
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setSuccess(false);
                response.setMessageLabel("auth_profile_expired_error_message");
                response.setDoLogout(true);
            } else {
                if (user.getRole().equals(Role.ADMIN)) {
                    response.setData(holidayController.getHolidaysByHolidayStatus(HolidayStatus.WAITING));
                } else if (user.getRole().equals(Role.MANAGER)) {
                    List<Holiday> result = new ArrayList<>();
                    user.getTeams().forEach(team -> {
                        team.getMembers().forEach(member -> {
                            result.addAll(holidayController.getHolidaysByHolidayStatusAndUser(HolidayStatus.WAITING, member));
                        });
                    });
                    response.setData(result);
                }
                response.setStatus(HttpStatus.OK);
                response.setShowToast(false);
                response.setSuccess(true);
            }

        }
        return response;
    }

    @GetMapping(path = "/rejectholidaysrequests")
    public ApiResponse<Holiday> rejectHolidayRequest(@RequestParam("id") Long holidayId, HttpServletRequest request) throws MessagingException {
        ApiResponse<Holiday> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Holiday holiday = holidayController.getHolidayById(holidayId);
            if (holiday == null || holiday.getUser().getHolidaySold() == null || holiday.getUser().getSicknessLeaverSold() == null) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            } else {
                String email = jwtUtil.extractUsername(token);
                User user = userController.getUserByEmail(email);
                float bookedHolidayDays = countDays( new Date(holiday.getFrom()),new Date(holiday.getTo()) );
                float currentUserAnnualLeaverSold = holiday.getUser().getHolidaySold();
                float currentUserSicknessLeaverSold = holiday.getUser().getSicknessLeaverSold();
                holiday.setStatus(HolidayStatus.REJECTED);
                holiday.setAt(new Date().getTime());
                if (holiday.getType().equals(HolidayType.ANNUAL_LEAVER))
                    holiday.getUser().setHolidaySold(currentUserAnnualLeaverSold + bookedHolidayDays);
                else if (holiday.getType().equals(HolidayType.SICKNESS_LEAVER))
                    holiday.getUser().setSicknessLeaverSold(currentUserSicknessLeaverSold +  bookedHolidayDays);

                holiday = holidayController.saveHoliday(holiday);
                holidayController.holidayRequestStatusChange(holiday.getUser(),user,HolidayStatus.REJECTED);
                emailController.sendHolidayApprovedEmail(holiday,false);
                response.setData(holiday);
                response.setStatus(HttpStatus.OK);
                response.setMessageLabel("book_holiday_rejected_toast_message");
                response.setSuccess(true);
            }

        }
        return response;
    }

    @GetMapping(path = "/approveholidaysrequests")
    public ApiResponse<Holiday> approveHolidayRequest(@RequestParam("id") Long holidayId, HttpServletRequest request) throws MessagingException {
        ApiResponse<Holiday> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Holiday holiday = holidayController.getHolidayById(holidayId);
            if (holiday == null || holiday.getUser().getHolidaySold() == null || holiday.getUser().getSicknessLeaverSold() == null) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            } else {
                String email = jwtUtil.extractUsername(token);
                User user = userController.getUserByEmail(email);
                holiday.setStatus(HolidayStatus.APPROVED);
                holiday.setAt(new Date().getTime());
                holiday = holidayController.saveHoliday(holiday);
                holidayController.holidayRequestStatusChange(holiday.getUser(),user,HolidayStatus.APPROVED);
                emailController.sendHolidayApprovedEmail(holiday,true);
                response.setData(holiday);
                response.setStatus(HttpStatus.OK);
                response.setMessageLabel("book_holiday_approved_toast_message");
                response.setSuccess(true);
            }

        }
        return response;
    }

    @GetMapping(path = "/cancelholidaysrequests")
    public ApiResponse<Holiday> cancelHolidayRequest(@RequestParam("id") Long holidayId, HttpServletRequest request) {
        ApiResponse<Holiday> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            Holiday holiday = holidayController.getHolidayById(holidayId);
            if (holiday == null || holiday.getUser().getHolidaySold() == null || holiday.getUser().getSicknessLeaverSold() == null) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
                response.setSuccess(false);
                response.setDoLogout(true);
            } else {
                String email = jwtUtil.extractUsername(token);
                User user = userController.getUserByEmail(email);
                float bookedHolidayDays = countDays( new Date(holiday.getFrom()),new Date(holiday.getTo()) );
                float currentUserAnnualLeaverSold = user.getHolidaySold();
                float currentUserSicknessLeaverSold = user.getSicknessLeaverSold();
                if (holiday.getType().equals(HolidayType.ANNUAL_LEAVER))
                    user.setHolidaySold(currentUserAnnualLeaverSold + bookedHolidayDays);
                else if (holiday.getType().equals(HolidayType.SICKNESS_LEAVER))
                    user.setSicknessLeaverSold(currentUserSicknessLeaverSold +  bookedHolidayDays);

                userController.save(user);
                User userTo;
                if(user.getTeam() == null) userTo = user.getCompany().getCompanyCreator();
                else userTo = user.getTeam().getManager();
                holidayController.holidayRequestStatusChange(userTo,holiday.getUser(),HolidayStatus.WAITING);
                holidayController.deleteHoliday(holiday);
                response.setData(holiday);
                response.setStatus(HttpStatus.OK);
                response.setMessageLabel("book_holiday_canceled_toast_message");
                response.setSuccess(true);
            }

        }
        return response;
    }

    public static float countDays(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        Calendar current = Calendar.getInstance();
        current.setTime(start);
        float countSelectedDays = 0;
        while (!current.getTime().after(end)) {
            countSelectedDays++;
            current.add(Calendar.DATE, 1);
        }
        return countSelectedDays;
    }

    @GetMapping(path = "/companysummary")
    public ApiResponse<CompanySummary> getCompanySummary(HttpServletRequest request) {
        ApiResponse<CompanySummary> response = new ApiResponse<>();
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.setData(null);
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setSuccess(false);
            response.setMessageLabel("auth_profile_expired_error_message");
            response.setDoLogout(true);
        } else {
            int employeesNumber = 0;
            int administratorsNumber = 0;
            int managersNumber = 0;
            String email = jwtUtil.extractUsername(token);
            User authUser = userController.getUserByEmail(email);

            CompanySummary summary = new CompanySummary();
            summary.setCompany(authUser.getCompany());
            summary.setCompanyCreator(authUser.getCompany().getCompanyCreator());
            summary.setTeam(authUser.getTeam());

            Map<String, Integer> numbers = new HashMap<>();
            for (User allCompanyUser : authUser.getCompany().getMembers()) {
                if (allCompanyUser.getRole().equals(Role.EMPLOYEE)) employeesNumber++;
                else if (allCompanyUser.getRole().equals(Role.MANAGER)) managersNumber++;
                else if (allCompanyUser.getRole().equals(Role.ADMIN)) administratorsNumber++;
            }
            numbers.put("employeesNumber",employeesNumber);
            numbers.put("administratorsNumber",administratorsNumber);
            numbers.put("managersNumber",managersNumber);
            summary.setUserNumbers(numbers);


            if(authUser.getTeam() != null) {
                summary.setTeamManager(authUser.getTeam().getManager());
                summary.setTeamMembers(authUser.getTeam().getMembers());
            }

            List<Document> documents = authUser.getDocumentsTo();
            documents.sort(Comparator.comparing(Document::getAt).reversed());
            List<Document> lastDocuments = new  ArrayList<>();
            for(int i = 0; i < documents.size(); i++){
                if(i < 4 ){
                    lastDocuments.add(documents.get(i));
                }else break;
            }
            summary.setLastDocuments(lastDocuments);

            int activityNumber = 0;
            int meetingNumber = 0;
            int eventNumber = 0;
            int taskNumber = 0;
            numbers = new HashMap<>();
            List<Event> allEvents = eventController.getEventsOfTheMonth();
            int stoppingIndex = 0;
            List<Event> lastEvents = new ArrayList<>();
            for (Event event : allEvents) {
                if (event.getParticipants().contains(authUser)) {
                    if(stoppingIndex < 4 ) {
                        lastEvents.add(event);
                        stoppingIndex++;
                    }
                    if(event.getEventType().equals(EventType.MEETING)) meetingNumber++;
                    else if(event.getEventType().equals(EventType.TASK)) taskNumber++;
                    else if(event.getEventType().equals(EventType.EVENT)) eventNumber++;
                    else if(event.getEventType().equals(EventType.ACTIVITY)) activityNumber++;
                }
            }
            numbers.put("meetingNumber",meetingNumber);
            numbers.put("taskNumber",taskNumber);
            numbers.put("eventNumber",eventNumber);
            numbers.put("activityNumber",activityNumber);
            summary.setEventNumbers(numbers);
            summary.setLastEvents(lastEvents);


            response.setData(summary);
            response.setStatus(HttpStatus.OK);
            response.setShowToast(false);
            response.setSuccess(true);

        }
        return response;
    }
}
