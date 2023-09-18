import React, { useState } from "react";
import { Form, Button, Col, Spinner } from 'react-bootstrap';
import Config from '../config.js';
import { useHistory} from 'react-router-dom';
import useToken from '../useToken';
import cogoToast from 'cogo-toast';


export default function ChangePassword() {
  const { token } = useToken();
  const [formData, setFormData] = useState({oldPassword:'', newPassword: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const history = useHistory();
  const [loading, setLoading] = useState(false);

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
    if (formData.password !== '' && formData.newPassword !== formData.confirmPassword) {
      setError("Password is not match");
      setLoading(false);
    } else {
      let resetPassRequest = {
        oldPassword: formData.oldPassword,
        newPassword: formData.newPassword
      };
      setError('');
      fetch(Config.apiUrl + '/changePassword', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(resetPassRequest)
      }).then(response => {
        if (!response.ok) { throw response }
        return response.json()
      })
      .then(data => {
        if(data.code === 200) {
          cogoToast.success(data.message, { position: 'top-center', heading: 'Success', hideAfter: 7 });
          setLoading(false);
          history.push('/payment');
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
          setError('can not change password');
        }
      })
    }
  }

  return (
    <Col md={{ span: 3, offset: 3 }} className="page-inner">
      <h3>Change Password</h3>
      <Form onSubmit={handleSubmit}>
      <Form.Group controlId="oldPassword" className="required">
          <Form.Label>Old Password</Form.Label>
          <Form.Control type="password" placeholder="Enter Old Password" value={formData.oldPassword} onChange={handleChange} />
        </Form.Group>
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
