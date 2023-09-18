import React, { useState } from "react";
import { Form, Button, Col, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import Config from '../config.js';
import PropTypes from 'prop-types';
import { useHistory } from 'react-router-dom';

Login.propTypes = {
  setToken: PropTypes.func.isRequired
}

export default function Login({ setToken }) {
  const [loginDetails, setLoginDetails] = useState({ username: '', password: '' });
  const [enterOtpRequest, setEnterOtpRequest] = useState({ otp: ''});
  const [enterOtpNumber, setEnterOtpNumber] = useState({ mobileNumber: '' });
  const [error, setError] = useState('');
  const history = useHistory();
  const [loading, setLoading] = useState(false);

  const [showLoginForm, setShowLoginForm] = useState(true);
  const [showOtpForm, setShowOtpForm] = useState(false);
  const [forceChangePassword, setForceChangePassword] = useState(false);
  const [userName, setUserName] = useState('');
  // showMobileOtpForm = false;

  const handleChange = (e) => {
    const { id, value } = e.target
    setLoginDetails(prevState => ({
      ...prevState,
      [id]: value
    }))
  }

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    var n = loginDetails.username;
    var p = loginDetails.password;
    var hasErrors = false;
    var errors = [];
    if (n.trim() == '') {
      errors.push('Please enter username.');
      hasErrors = true;
    }

    if (p.trim() == '') {
      errors.push('Please enter password.');
      hasErrors = true;
    }

    if (hasErrors) {
      setError(errors.join('\r\n'));
    } else {

      setLoading(true);
      fetch(Config.apiUrl + '/authenticate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginDetails)
      }).then(response => {
        if (!response.ok) { throw response }
        return response.json()
      })
        .then(data => {
          setShowLoginForm(false);
          setShowOtpForm(true);
          setLoading(false);
          setUserName(data.details.login);
           if(data.details.forceChangePassword && data.details.forceChangePassword === true) {
            setForceChangePassword(true)
          }
        })
        .catch(err => {
          setLoading(false);
          if (err.text) {
            err.text().then(errorMessage => {
              setError(errorMessage);
            })
          } else {
            setError('login Error');
          }
        })

    }
  }

   const handleChange3 = (e) => {
    const { id, value } = e.target
    setEnterOtpRequest(prevState => ({
      ...prevState,
      [id]: value
    }))
  }

  const handleSubmit3 = async e => {
    e.preventDefault();
    setError('');
    var o = enterOtpRequest.otp;
    var hasErrors = false;
    var errors = [];
    if (o.trim() == '') {
      errors.push('Please enter one time password.');
      hasErrors = true;
    }

    if (hasErrors) {
      setError(errors.join('<br/>'));
    } else {
      setLoading(true);
      enterOtpRequest.userName = userName;
      fetch(Config.apiUrl + '/verifyotp', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(enterOtpRequest)
      }).then(response => {
        console.log(response);
        if (!response.ok && response.status === 401) 
          { throw response }
        return response.json()
      })
        .then(data => {
          setLoading(false);
          if(data.code === 200) {
            setToken(data.details.token);
            if(forceChangePassword && forceChangePassword === true) {
              history.push('/change-password');
            } else {
              history.push('/payment');
            }
          } else {
            setError(data.message);
          }
        })
        .catch(err => {
          setLoading(false);
          if (err.text) {
            err.text().then(errorMessage => {
              setError(errorMessage);
            })
          } else {
            setError('can not send request');
          }
        })
      }
  }

  return (
    <Col md={{ span: 3, offset: 3 }} className="page-inner">
       <div id="loginForm"  style={showLoginForm ? {} : {display: "none"}}>
      <h3>Login</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="username">
          <Form.Label>User</Form.Label>
          <Form.Control type="text" name="username" placeholder="Enter user" value={loginDetails.username} onChange={handleChange} />
        </Form.Group>

        <Form.Group controlId="password">
          <Form.Label>Password</Form.Label>
          <Form.Control type="password" name="password" placeholder="Enter password" value={loginDetails.password} onChange={handleChange} />
        </Form.Group>
        {error !== '' && (<div className="text-danger">{error}</div>)}
        {loading === true ? <Button variant="primary" block disabled>
          <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true"/>
        Loading...
        </Button> :
        <Button variant="primary" type="submit" block>
          Submit
        </Button>}
          <div className="forgot-pass text-right">
            <Link to="/forgot-password">Forgot Password</Link>
          </div>
      </Form>
      </div>
      
      <div id="otpForm"  style={showOtpForm ? {} : {display: "none"}}>
      <h3>Enter OTP</h3>
      <Form onSubmit={handleSubmit3}>
        <Form.Group controlId="otp">
          <Form.Label>OTP</Form.Label>
          <Form.Control type="text" name="otp" placeholder="Enter OTP" value={enterOtpRequest.otp} onChange={handleChange3} />
        </Form.Group>

        
        {error !== '' && (<div className="text-danger">{error}</div>)}
        {loading === true ? <Button variant="primary" block disabled>
        <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true"/>
        Loading...
      </Button> :
        <Button variant="primary" type="submit" block>
          Submit
        </Button>}

        <Button variant="primary" onClick={() => window.location.reload()} block>
          Back
        </Button>
      </Form></div>
    </Col>
  );
}
