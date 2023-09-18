import React, { useState } from "react";
import { Form, Button, Col, Spinner } from 'react-bootstrap';
import Config from '../config.js';


export default function EnterOtpNumber() {
  const [enterOtpNumber, setEnterOtpNumber] = useState({ mobileNumber: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { id, value } = e.target
    setEnterOtpNumber(prevState => ({
      ...prevState,
      [id]: value
    }))
  }

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    setLoading(true);
    fetch(Config.apiUrl + '/generateotp', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(enterOtpNumber)
    }).then(response => {
      console.log(response);
      if (!response.ok && response.status === 401) 
        { throw response }
      return response.json()
    })
      .then(data => {
        setLoading(false);
        if(data.code === 200) {
          setSuccessMessage(data.message);
          history.push('/enter-otp');
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

  return (
    <Col md={{ span: 3, offset: 3 }} className="page-inner">
   
    { successMessage === '' ?
      <div>
      <h3>Enter Mobile Number for OTP</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="mobileNumber">
          <Form.Label>Mobile Number</Form.Label>
          <Form.Control type="text" name="mobileNumber" placeholder="Enter Mobile Number" value={enterOtpNumber.mobileNumber} onChange={handleChange} />
        </Form.Group>
        {error !== '' && (<div className="text-danger">{error}</div>)}
        {loading === true ? <Button variant="primary" block disabled>
        <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true"/>
        Loading...
      </Button> :
        <Button variant="primary" type="submit" block>
          Submit
        </Button>}
      </Form></div> :
      <div> {successMessage} </div>
    }
    </Col>
  );
}
