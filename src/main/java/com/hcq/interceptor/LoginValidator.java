package com.hcq.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {
	@Override
	protected void validate(Controller c) {
		validateRequiredString("username","nameError","username is null");
		validateRequiredString("password","passError","username is null");
	}
	@Override
	protected void handleError(Controller c) {
	}
}
