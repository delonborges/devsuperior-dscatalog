package com.delonborges.dscatalog.services.validations;

import com.delonborges.dscatalog.dto.InsertUserDTO;
import com.delonborges.dscatalog.entities.User;
import com.delonborges.dscatalog.repositories.UserRepository;
import com.delonborges.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class InsertUserValidator implements ConstraintValidator<InsertUserValidation, InsertUserDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(InsertUserValidation ann) {
    }

    @Override
    public boolean isValid(InsertUserDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();
        User user = userRepository.findByEmail(dto.getEmail());
        if (user != null) {
            list.add(new FieldMessage("email", "Email already registered"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}