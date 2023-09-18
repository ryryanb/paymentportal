import React, { useState } from "react";
import { Form, Button, Col, Spinner } from 'react-bootstrap';
import Config from '../config.js';
import { useHistory} from 'react-router-dom';
import useToken from '../useToken';
import cogoToast from 'cogo-toast';


export default function Register() {
  const { token } = useToken();
  const [formData, setFormData] = useState({userName:'', password: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const history = useHistory();
  const [loading, setLoading] = useState(false);
  const [billerRegistered, setBillerRegistered] = useState(false);

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
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords mismatch");
      setLoading(false);
    } else {
      var l1 = window.location.href;
      var l2 = l1.split("?");
      var billeridfound = false;
      var billerid = '';
      if (typeof l2[1] != 'undefined' && l2[1].indexOf('billerid') != -1) {
        var l3 = l2[1].split("&");

        for (var i in l3) {
          if (l3[i].indexOf('billerid') != -1) {
            var l4 = l3[i].split("=");
            if (typeof l4[1] != 'undefined' && l4[1] != '') {
              billeridfound = true;
              billerid = l4[1];
            }
          }
        }
      }
    if (billeridfound) {
      let registerRequest = {
        userName: formData.userName,
        password: formData.password,
        confirmPassword: formData.confirmPassword,
        billerId: billerid
      };
      setError('');
      fetch(Config.apiUrl + '/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(registerRequest)
        }).then(response => {
        if (!response.ok) { throw response }
        return response.json()
      })
      .then(data => {
        if(data.code === 200) {
          cogoToast.success(data.message, { position: 'top-center', heading: 'Success', hideAfter: 7 });
          setLoading(false);
          setBillerRegistered(true);
          // history.push('/payment');
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

    } else {
      setError('Biller id not found');
    }
      
      
    }
  }

  return (
    <Col md={{ span: 3, offset: 3 }} className="page-inner">
      <div id="registrationForm"  style={!billerRegistered ? {} : {display: "none"}}>
        <h3>Register to Biller Portal</h3>
        <Form onSubmit={handleSubmit}>
        <Form.Group controlId="userName" className="required">
            <Form.Label>User Name</Form.Label>
            <Form.Control type="text" placeholder="Enter User Name" value={formData.userName} onChange={handleChange} />
          </Form.Group>
          <Form.Group controlId="password" className="required">
            <Form.Label>Password</Form.Label>
            <Form.Control type="password" placeholder="Enter Password" value={formData.password} onChange={handleChange} />
            <Form.Text className="text-muted">
              Must be at least 8 characters long.
              </Form.Text>
          </Form.Group>
          <Form.Group controlId="confirmPassword" className="required">
            <Form.Label>Confirm Password</Form.Label>
            <Form.Control type="password" value={formData.confirmPassword} onChange={handleChange} placeholder="Confirm Password"/>
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
      </div>
      <div id="registrationSuccess"  style={billerRegistered ? {} : {display: "none"}}>
        Registration Successful! Click <a href="#">here</a> to login.
      </div>
    </Col>
  );
}
