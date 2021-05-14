package com.criteria.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice(annotations = { RestController.class })
public class ExceptionsControllerAdvice {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionsControllerAdvice.class);

	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<RestErrorResponse> handleBindingErrors(final RuntimeException ex) {
		LOGGER.trace(">>handleBindingErrors()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		response.setErrParam(ex.getMessage());
		LOGGER.trace("<<handleBindingErrors()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<RestErrorResponse> handleMethodArgumentException(final MethodArgumentNotValidException ex) {
		LOGGER.trace(">>handleMethodArgumentException()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		String errorField = ex.getBindingResult().getFieldErrors().get(0).getField();
		response.setErrParam(errorField);
		response.setErrMessage(errorMessage);
		LOGGER.trace("<<handleMethodArgumentException()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	public ResponseEntity<RestErrorResponse> handleMediaTypeNotSupportedException(
			final HttpMediaTypeNotSupportedException ex) {
		LOGGER.trace(">>handleMediaTypeNotSupportedException()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		LOGGER.trace("<<handleMediaTypeNotSupportedException()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ResponseEntity<RestErrorResponse> handleMessageNotReadableException(
			final HttpMessageNotReadableException ex) {
		LOGGER.trace(">>handleMessageNotReadableException()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		LOGGER.trace("<<handleMessageNotReadableException()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<RestErrorResponse> handleGlobalErrors(final Exception ex) {
		LOGGER.trace(">>handleGlobalErrors()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		response.setErrMessage(ex.getMessage());
		if (ex instanceof java.lang.NullPointerException) {
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		LOGGER.trace("<<handleGlobalErrors()");
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<RestErrorResponse> handleMethodArgumentTypeMismatchException(HttpServletRequest request,
			MethodArgumentTypeMismatchException ex) {
		LOGGER.trace(">>handleMethodArgumentTypeMismatchException()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		String param = ex.getName();
		response.setErrMessage(param);
		LOGGER.trace("<<handleMethodArgumentTypeMismatchException()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public ResponseEntity<RestErrorResponse> handleIllegalArgumentException(HttpServletRequest request,
			IllegalArgumentException exception) {
		LOGGER.trace(">>handleIllegalArgumentException()");
		LOGGER.error(exception.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		LOGGER.trace("<<handleIllegalArgumentException()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ ConstraintViolationException.class })
	@ResponseBody
	public ResponseEntity<RestErrorResponse> handleConstraintViolationException(final ConstraintViolationException ex) {
		LOGGER.trace(">>handleConstraintViolationException()");
		LOGGER.error(ex.getMessage());
		RestErrorResponse response = new RestErrorResponse();
		LOGGER.trace("<<handleConstraintViolationException()");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
