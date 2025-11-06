package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.Company;
import com.demo.springbootdemo.entity.Role;
import com.demo.springbootdemo.entity.Team;
import com.demo.springbootdemo.entity.User;
import com.demo.springbootdemo.model.ApiResponse;
import com.demo.springbootdemo.repository.CompanyRepository;
import com.demo.springbootdemo.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TeamRepository teamRepository;

    public Company isCompanyExist(Company company){
        boolean isExist = companyRepository.findByNameIgnoreCase(company.getName().trim()).isEmpty();
        if(!isExist){
            return null;
        }
        return company;
    }

    public Company getCompanyById(Long companyId){
        return companyRepository.findById(companyId).orElse(null);
    }

    public ApiResponse<Company> setCompany(Company company){
        ApiResponse<Company> response = new ApiResponse<>();
        Company companyById = companyRepository.findById(company.getId()).orElse(null);
        if(companyById == null){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessageLabel("error_status_INTERNAL_SERVER_ERROR");
            response.setSuccess(false);
            response.setDoLogout(true);
        }else{
            if(!companyById.getName().equals(company.getName())){
                Company existingCompany = isCompanyExist(company);
                if(existingCompany == null){
                    response.setStatus(HttpStatus.CONFLICT);
                    response.setMessageLabel("auth_signup_used_company_name_error_message");
                    response.setData(null);
                    response.setSuccess(false);
                }else{
                    response = changeCompanyDetails(company);
                }
            }else{
                response = changeCompanyDetails(company);
            }
        }
        return response;
    }

    private ApiResponse<Company> changeCompanyDetails(Company company){
        ApiResponse<Company> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK);
        response.setMessageLabel("manage_company_edit_success_message");
        response.setData(companyRepository.save(company));
        response.setSuccess(true);
        return response;
    }

    public List<Team> getTeams(Company company,User user){
        List<Team> teams = new ArrayList<>();
        if(user.getEmail().equals(company.getCompanyCreator().getEmail()) || user.getRole().equals(Role.ADMIN))
            teams.addAll(teamRepository.findByCompany(company));
        else if(user.getRole().equals(Role.MANAGER))
            teams.addAll(teamRepository.findByManager(user));
        return teams;
    }

    public Company saveCompany(Company company){
        return  companyRepository.save(company);
    }

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    public Company getMembersByUser(User authUser) {
        return companyRepository.findByMembersContains(authUser);
    }
}
