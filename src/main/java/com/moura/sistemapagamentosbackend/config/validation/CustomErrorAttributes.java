package com.moura.sistemapagamentosbackend.config.validation;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.moura.sistemapagamentosbackend.model.error.ErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, ErrorAttributeOptions options) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", new Date());
        addStatus(errorAttributes, requestAttributes);

        Throwable throwable = super.getError(requestAttributes);
        if (throwable instanceof BindingResult) {
            addErrors(errorAttributes, (BindingResult) throwable, locale);
        } else if (throwable instanceof MethodArgumentNotValidException) {
            addErrors(errorAttributes, ((MethodArgumentNotValidException) throwable).getBindingResult(), locale);
        } else if (throwable instanceof InvalidFormatException) {
            addErrors(errorAttributes, (InvalidFormatException) throwable, locale);
        } else if (throwable instanceof HttpMessageNotReadableException && throwable.getCause() instanceof InvalidFormatException) {
            addErrors(errorAttributes, (InvalidFormatException) throwable.getCause(), locale);
        } else if (throwable instanceof HttpMessageNotReadableException && throwable.getCause() instanceof MismatchedInputException) {
            addErrors(errorAttributes, (MismatchedInputException) throwable.getCause(), locale);
        } else if (throwable instanceof RuntimeException) {
            addErrors(errorAttributes, (RuntimeException) throwable, locale);
        }

        return errorAttributes;
    }

    private void addStatus(Map<String, Object> errorAttributes, WebRequest requestAttributes) {
        Integer status = getAttribute(requestAttributes,
                "javax.servlet.error.status_code");
        if (status == null) {
            errorAttributes.put("status", 999);
            errorAttributes.put("error", "None");
            return;
        }
        errorAttributes.put("status", status);
        try {
            errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
        }
        catch (Exception ex) {
            // Unable to obtain a reason
            errorAttributes.put("error", "Http Status " + status);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(WebRequest requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    private void addErrors(Map<String, Object> errorAttributes, BindingResult bindingResult, Locale locale) {
        LinkedHashMap<String, List<ErrorResponseDTO>> errors = new LinkedHashMap();
        int unknowFieldIndex = 0;

        for (ObjectError error : bindingResult.getAllErrors()) {

            ErrorResponseDTO e = new ErrorResponseDTO();
            e.setCode(error.getCode());
            e.setMessage(localizedMessage(error, locale));

            String fieldName = "";

            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                fieldName = fieldError.getField();
                e.setRejectedValue(fieldError.getRejectedValue());
            } else {
                fieldName = "unknowField" + unknowFieldIndex++;
                e.setRejectedValue("unknowValue");
            }

            if(errors.containsKey(fieldName)) {
                List<ErrorResponseDTO> fieldErrors = new LinkedList<>(errors.get(fieldName));
                fieldErrors.add(e);
                errors.put(fieldName, fieldErrors);

            } else {
                errors.put(fieldName, List.of(e));
            }
        }

        errorAttributes.put("errors", errors);
    }

    private void addErrors(Map<String, Object> errorAttributes, InvalidFormatException ex, Locale locale) {
        LinkedHashMap<String, List<ErrorResponseDTO>> errors = new LinkedHashMap();

        String getTargetTypeName = ex.getTargetType().getName();
        Object getTargetValue = ex.getValue();
        if(ex.getTargetType() != null){
            if(!ex.getPath().isEmpty()){
                if(ex.getPath().size() > 1){
                    getTargetTypeName = ex.getPath().get(ex.getPath().size()-1).getDescription().split("\\[")[0];

                    getTargetValue = localizedMessage(
                            "error.InvalidArrayValueException",
                            new Object[] {ex.getValue(), ex.getPath().get(ex.getPath().size()-1).getDescription()},
                            locale)
                    ;
                }
            }
        }

        ErrorResponseDTO e = new ErrorResponseDTO();
        e.setCode("InvalidFormatException");
        String message = localizedMessage(
                "error.InvalidFormatException",
                new Object[] {getTargetTypeName, getTargetValue},
                locale);

        if(ex.getTargetType()!=null && ex.getTargetType().isEnum()){
            message = String.format("%s. %s.",
                    message,
                    localizedMessage(
                            "error.InvalidEnumValueException",
                            new Object[] {
                                    getTargetValue,
                                    getInvalidFormatExceptionFieldName(ex.getPath()),
                                    Arrays.toString(ex.getTargetType().getEnumConstants())
                            },
                            locale
                    )
            );
        }

        e.setMessage(message);
        String fieldName = getInvalidFormatExceptionFieldName(ex.getPath());
        e.setRejectedValue(ex.getValue());
        errors.put(fieldName, List.of(e));
        errorAttributes.put("errors", errors);
    }

    private void addErrors(Map<String, Object> errorAttributes, MismatchedInputException ex, Locale locale) {
        LinkedHashMap<String, List<ErrorResponseDTO>> errors = new LinkedHashMap();

        ErrorResponseDTO e = new ErrorResponseDTO();
        e.setCode("MismatchedInputException");
        String message = localizedMessage(
                "error.InvalidFormatException",
                new Object[] {ex.getTargetType().getName()},
                locale);

        e.setMessage(message);
        String fieldName = getInvalidFormatExceptionFieldName(ex.getPath());
        errors.put(fieldName, List.of(e));
        errorAttributes.put("errors", errors);
    }

    private void addErrors(Map<String, Object> errorAttributes, RuntimeException ex, Locale locale) {
        LinkedHashMap<String, List<ErrorResponseDTO>> errors = new LinkedHashMap();

        ErrorResponseDTO e = new ErrorResponseDTO();
        String message = localizedMessage(
                ex.getMessage(),
                locale);

        e.setMessage(message);
        errors.put("internal", List.of(e));
        errorAttributes.put("errors", errors);
    }

    private String getInvalidFormatExceptionFieldName(List<JsonMappingException.Reference> path) {

        for (JsonMappingException.Reference r : path) {
            return r.getFieldName();
        }

        return null;
    }

    private String localizedMessage(ObjectError error, Locale locale) {
        return messageSource.getMessage(error, locale);
    }

    private String localizedMessage(String message, Object[] args, Locale locale) {
        return messageSource.getMessage(message, args, locale);
    }

    private String localizedMessage(String message, Locale locale) {
        return messageSource.getMessage("", null, message, locale);
    }
}
