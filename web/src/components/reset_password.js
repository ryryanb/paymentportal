import React, { useState } from "react";
import { Form, Button, Col, Spinner } from 'react-bootstrap';
import Config from '../config.js';
import { useHistory, useParams } from 'react-router-dom';


export default function ResetPasword() {
  const   { resetToken } = useParams();
  const [formData, setFormData] = useState({newPassword: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const history = useHistory();
  const [loading, setLoading] = useState(false);

  console.log(resetToken)

  const handleChange = (e) => {
    const { id, value } = e.target
    setFormData(prevState => ({
      ...prevState,
      [id]: value
    }))
  }

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    if (formData.newPassword !== formData.confirmPassword) {
      setError("Password is not match");
      setLoading(false);
    } else {
      let resetPassRequest = {
        token: resetToken,
        newPassword: formData.newPassword
      };
      setError('');
      fetch(Config.apiUrl + '/resetPassword', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(resetPassRequest)
      }).then(response => {
        if (!response.ok && response.status === 401) 
          { throw response }
        return response.json()
      })
      .then(data => {
        if(data.code === 200) {
          history.push('/login');
        } else {
          setError(data.message);
          setLoading(false);
        }
      })
      .catch(err => {
        setLoading(false);
        if (err.text) {
          err.text().then(errorMessage => {
            setError(errorMessage);
          })
        } else {
          setError('can not reset password');
        }
      })
    }
  }

  return (
    <Col md={{ span: 3, offset: 3 }} className="page-inner">
      <h3>Reset Password</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="newPassword" className="required">
          <Form.Label>New Password</Form.Label>
          <Form.Control type="password" placeholder="Enter New Password" value={formData.newPassword} onChange={handleChange} />
          <Form.Text className="text-muted">
            Must be at least 8 characters long.
            </Form.Text>
        </Form.Group>
        <Form.Group controlId="confirmPassword" className="required">
          <Form.Label>Confirmation</Form.Label>
          <Form.Control type="password" value={formData.confirmPassword} onChange={handleChange} placeholder="Confirm New Password"/>
        </Form.Group>
        {error !== '' && (<div className="text-danger">{error}</div>)}
        {loading === true ? <Button variant="primary" block disabled>
          <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true"/>
        Loading...
        </Button> :
        <Button variant="primary" type="submit" block>
          Submit
        </Button>}
      </Form>
    </Col>
  );
}
