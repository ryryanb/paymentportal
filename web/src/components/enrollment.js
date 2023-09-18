import React, { useReducer, useRef, useState } from "react";
import { Form, Button, Card, Col, Spinner } from 'react-bootstrap';
import Config from '../config.js';
import { useHistory } from 'react-router-dom';
import cogoToast from 'cogo-toast';

import { Validation, Validator, ValidationHelper } from "../validation";

const initialFrom = () => ({
  address: '', companyName: '', contactName: '', email: '', url: '', generatePassword: false,
    login: '', mustChangePassword: false, password: '', confirmPassword: '', phone: '', sendAccountInfo: false
});

const formReducer = (state, action) => {
  let newValue = {};
  newValue[action.name] = action.value;
  return Object.assign({}, state, newValue);
};

const errorReducer = (allError, error) => {
  return Object.assign({}, allError, error);
};

export default function Enrollment() {
  let validationRef = useRef(null);

  let [enrollData, setEnrollData] = useReducer(formReducer, {}, initialFrom);
  let [error, setError] = useReducer(errorReducer, {});

  const history = useHistory();
  const [loading, setLoading] = useState(false);
  const [submitError, setSubmitError] = useState('');

  const handleChange = evt => {
    setEnrollData({
      name: evt.target.name,
      value: evt.target.value
    });
  };

  const handleCheckbox = (e) => {
    const { name, checked } = e.target
    if(name === 'generatePassword' && checked === true) {
      setEnrollData({name: 'password', value: ''});
      setEnrollData({name: 'confirmPassword', value: ''});
    }
    setEnrollData({
      name: name,
      value: checked
    });
  }

  const onValidate = err => {
    setError(err);
  };

  const handleSubmit = async e => {
    e.preventDefault();
    let formValid = validationRef.current.validate();
    console.log(validForm(formValid));
    if(validForm(formValid) === true) {
      setLoading(true);
      setSubmitError('');
      fetch(Config.apiUrl + '/user/enroll', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(enrollData)
      }).then(response => {
        return response.json()
      })
      .then(data => {
        if (data.code && data.code === 200) {
          console.log("enroll successful");
          if(enrollData.generatePassword === true) {
            cogoToast.success("New password is sent to the registration email.", 
            { position: 'top-center', heading: 'Success', hideAfter: 7});
          }
          history.push('/login');
        } else {
          setLoading(false);
          if (data.message) {
            setSubmitError(data.message);
          } else {
            setSubmitError("can not enroll user");
          }
        }
        console.log(data);
      })
      .catch(err => {
        setLoading(false);
        setSubmitError(err.message);
      })
    }
  };

  const numberValidation = value => {
    return isNaN(value.trim()) ? "Value should be number" : "";
  };

  const emailValidation = value => {
    return !/\S+@\S+\.\S+/.test(value) ? "Invalid email" : "";
  };

  const passwordValidation = value => {
    if(enrollData.generatePassword === false) {
      if(value === "") {
        return "Password is required";
      }else if(value.length < 8) {
        return "Password must be at least 8 characters long"
      } else {
        return "";
      } 
    }
    return "";
  };

  const confirmPasswordValidation = value => {
    if(enrollData.generatePassword === false) {
      if(value === "") {
        return "Confirm password is required";
      }else if(value !== enrollData.password) {
        return "Password is not match";
      } else {
        return "";
      } 
    }
    return "";
  };

  const validForm = (object) => {
    return !Object.values(object).some(x => (x !== null && x !== ''));
  }

  return (
    <Col md={{ span: 5, offset: 3 }} className="page-inner">
      <h3>Enrollment</h3>
      <Form onSubmit={handleSubmit}>
        <Validation ref={validationRef}>
          <Card>
            <Card.Header as="h5">Information</Card.Header>
            <Card.Body>
              <Form.Group className="required">
                <Form.Label>Login</Form.Label>
                <Validator name="login" value={enrollData.login} validations={[ValidationHelper.required("Login is required")]} 
                  onValidate={onValidate}>
                  <Form.Control type="text" name="login" value={enrollData.login} onChange={handleChange}/>
                </Validator>
                {error.login && <span className="error text-danger">{error.login}</span>}
              </Form.Group>

              <Form.Group className="required">
                <Form.Label>Company name</Form.Label>
                <Validator name="companyName" value={enrollData.companyName} validations={[ValidationHelper.required("Company name is required")]} 
                  onValidate={onValidate}>
                  <Form.Control type="text" name="companyName" value={enrollData.companyName} onChange={handleChange}/>
                </Validator>
                {error.companyName && <span className="error text-danger">{error.companyName}</span>}
              </Form.Group>

              <Form.Group className="required">
                <Form.Label>Address</Form.Label>
                <Validator name="address" value={enrollData.address} validations={[ValidationHelper.required("Address is required")]} 
                  onValidate={onValidate}>
                  <Form.Control type="text" name="address" value={enrollData.address} onChange={handleChange}/>
                </Validator>
                {error.address && <span className="error text-danger">{error.address}</span>}
              </Form.Group>

              <Form.Group className="required">
                <Form.Label>Contact name</Form.Label>
                <Validator name="contactName" value={enrollData.contactName} validations={[ValidationHelper.required("Contact name is required")]}
                  onValidate={onValidate}>
                <Form.Control type="text" name="contactName" value={enrollData.contactName} onChange={handleChange} />
              </Validator>
              {error.contactName && <span className="error text-danger">{error.contactName}</span>}
              </Form.Group>

              <Form.Group className="required">
                <Form.Label>Email</Form.Label>
                <Validator name="email" value={enrollData.email} validations={[ValidationHelper.required("Email is required"), emailValidation]}
                  onValidate={onValidate}>
                  <Form.Control type="text" name="email" value={enrollData.email} onChange={handleChange} />
                </Validator>
                {error.email && <span className="error text-danger">{error.email}</span>}
              </Form.Group>

              <Form.Group className="required">
                <Form.Label>Phone</Form.Label>
                <Validator name="phone" value={enrollData.phone} validations={[ValidationHelper.required("Phone is required"),
                  numberValidation]} 
                  onValidate={onValidate}>
                  <Form.Control type="text" name="phone" value={enrollData.phone} onChange={handleChange}/>
                </Validator>
                {error.phone && <span className="error text-danger">{error.phone}</span>}
              </Form.Group>

              <Form.Group>
                <Form.Label>URL</Form.Label>
                <Form.Control type="text" name="url" value={enrollData.url} onChange={handleChange} />
              </Form.Group>
            </Card.Body>
          </Card>

          <Card className="mt-3">
            <Card.Header as="h5">Authentication</Card.Header>
            <Card.Body>

              <Form.Group className="required">
                <Form.Label>Password</Form.Label>
                <Validator name="password" value={enrollData.password} validations={[passwordValidation]}
                    onValidate={onValidate}>
                  <Form.Control type="password" name="password" placeholder="Password" value={enrollData.password} onChange={handleChange} readOnly={enrollData.generatePassword} />
                  <Form.Text className="text-muted">
                    Must be at least 8 characters long.
                  </Form.Text>
                </Validator>
                {error.password && <span className="error text-danger">{error.password}</span>}
              </Form.Group>

              <Form.Group className="required">
                <Form.Label>Confirmation</Form.Label>
                <Validator name="confirmPassword" value={enrollData.confirmPassword} validations={[confirmPasswordValidation]}
                    onValidate={onValidate}>
                  <Form.Control type="password" name="confirmPassword" value={enrollData.confirmPassword} onChange={handleChange} placeholder="Confirm Password" readOnly={enrollData.generatePassword}/>
                </Validator>
                {error.confirmPassword && <span className="error text-danger">{error.confirmPassword}</span>}
              </Form.Group>

              <Form.Group>
                <Form.Check type="checkbox" name="generatePassword" label="Generate Password" onChange={handleCheckbox} />
              </Form.Group>
              <Form.Group>
                <Form.Check type="checkbox" name="mustChangePassword" label="Must change password at next logon" value={enrollData.mustChangePassword} onChange={handleCheckbox} />
              </Form.Group>
            </Card.Body>
          </Card>
          <Form.Group className="pt-2">
            <Form.Check type="checkbox" name="sendAccountInfo" label="Send account information to the user" value={enrollData.sendAccountInfo} onChange={handleCheckbox} />
          </Form.Group>
        </Validation>
        {submitError !== '' && (<div className="error text-danger">{submitError}</div>)}
        {loading === true ? <Button variant="primary" block disabled>
          <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true"/>
        Loading...
        </Button> : 
        <Button variant="primary" type="submit" className="mt-3" block>
          Submit
          </Button>}
      </Form>
    </Col>
  );
}
