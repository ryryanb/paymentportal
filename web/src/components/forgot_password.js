import React, { useState } from "react";
import { Form, Button, Col, Spinner } from 'react-bootstrap';
import Config from '../config.js';


export default function ForgotPasword() {
  const [forgotPassRequest, setForgotPassRequest] = useState({ userName: '', email: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { id, value } = e.target
    setForgotPassRequest(prevState => ({
      ...prevState,
      [id]: value
    }))
  }

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    setLoading(true);
    fetch(Config.apiUrl + '/forgotPassword', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(forgotPassRequest)
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
      <h3>Forgot Password</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="userName">
          <Form.Label>User</Form.Label>
          <Form.Control type="text" name="userName" placeholder="Enter user" value={forgotPassRequest.userName} onChange={handleChange} />
        </Form.Group>

        <Form.Group controlId="email">
          <Form.Label>Email</Form.Label>
          <Form.Control type="email" name="email" placeholder="Enter email" value={forgotPassRequest.email} onChange={handleChange} />
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
