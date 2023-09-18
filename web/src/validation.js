import React, { useEffect, useImperativeHandle, forwardRef, useRef } from "react";

export const Validation = forwardRef(function Validation({ children }, ref) {
  const validatorRefs = useRef({});

  const validate = () => {
    let allError = {};
    Object.values(validatorRefs.current).forEach(validationRef => {
      //let error = validationRef ? validationRef.triggerValidate() : {};
      let error = validationRef.validate();
      //console.log('Individual error', error);
      allError = Object.assign(allError, error);
    });
    //console.log("All error", this.error);
    return allError;
  };

  useImperativeHandle(ref, () => ({
    validate
  }), []);

  const getChildrenRef = validator => {
    return validatorRef => {
      //Ref callback will be called with null when componenet unmount or update since we return new function
      if (!validatorRef) {
        Reflect.deleteProperty(validatorRefs.current, validator.props.name);
        return;
      }
      validatorRefs.current[validator.props.name] = validatorRef;
    };
  };

  const getValidationChildRef = children => {
    // remove all old validationRef when component re-renders
    validatorRefs.current = {};

    if (!children) return children;
    const type = typeof children;
    if (type === "boolean") {
      return children;
    }
    if (type === "string" || type === "number") {
      return children;
    }
    return React.Children.map(children, child => {
      if (!React.isValidElement(child)) {
        return child;
      }
      if (child && child.type === Validator) {
        return React.cloneElement(child, { ref: getChildrenRef(child) });
      } else if (child.props.children) {
        let result = getValidationChildRef(child.props.children);
        if (result.length === 0) {
          return child;
        } else if (result.length === 1) {
          result = result[0];
        }
        return React.cloneElement(child, {}, result);
      }
      return child;
    });
  };

  return getValidationChildRef(children);
});

export const Validator = forwardRef(function Validator({ name, value, validations = [], onValidate = () => {}, children }, ref) {
  let valueRef = useRef(value);

  useEffect(() => {
    //simulating new and old value props check
    if (isDifferent(valueRef.current, value)) {
      valueRef.current = value;
      validate();
    }
  });

  useImperativeHandle(ref, () => ({
    validate
  }));

  const validate = () => {
    let error = {};
    for (let validation of validations) {
      error[name] = validation(value);
      if (error[name]) break;
    }
    onValidate(error);
    return error;
  };

  const isDifferent = (oldValue, newValue) => {
    if (Array.isArray(oldValue)) {
      return oldValue.length !== newValue.length;
    }
    //console.log('oldValue , newValue', oldValue, newValue);
    return oldValue !== newValue;
  };

  return children;
});

export const ValidationHelper = {
  required: (errorMessage = "Value is required") => {
    return value => {
      return Boolean(value) ? "" : errorMessage;
    };
  }
};

