package com.delonborges.dscatalog.services.validations;

import com.delonborges.dscatalog.dto.UpdateUserDTO;
import com.delonborges.dscatalog.entities.User;
import com.delonborges.dscatalog.repositories.UserRepository;
import com.delonborges.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateUserValidator implements ConstraintValidator<UpdateUserValidation, UpdateUserDTO> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UpdateUserValidation ann) {
    }

    @Override
    public boolean isValid(UpdateUserDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked") var uriAttributes = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userId = Long.parseLong(uriAttributes.get("id"));

        List<FieldMessage> list = new ArrayList<>();
        User user = userRepository.findByEmail(dto.getEmail());
        if (user != null && userId != user.getId()) {
            list.add(new FieldMessage("email", "Email already registered"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();
        }
        return list.isEmpty();
    }
}