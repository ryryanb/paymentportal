import React, { useState } from "react";
import './App.css';
import { Switch, Route, NavLink, Redirect} from 'react-router-dom';
import {Navbar, Nav} from 'react-bootstrap';
import Login from './components/login';
import Enrollment from './components/enrollment';
import Payment from './components/payment';
import ForgotPassword from './components/forgot_password';
import ResetPassword from './components/reset_password';
import Register from './components/register';
import useToken from './useToken';
import utils from './utils';
import ChangePassword from './components/change_password';


function App() {
  const { token, setToken } = useToken();
  return (
    <div className="app wrapper">
        <Navbar bg="light" expand="lg">
          <Navbar.Brand href="/psbbiller">Third party Billers </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav"/>
          <Nav.Item className="ml-auto">
          {token && <Nav.Link as={NavLink} to="/logout" onClick={utils.logout}>Logout</Nav.Link>}
          </Nav.Item>
         </Navbar>
      {!token ?<Switch>
        <Route exact path="/" render={() => {
            return (
              <Redirect to="/login" />
            )
          }
        }
        />
          <Route path={"/login"}>
            <Login setToken={setToken} />
          </Route>
      <Route path="/enrollment" component={Enrollment}></Route>
      <Route path="/forgot-password" component={ForgotPassword}></Route>
      <Route path="/register" component={Register}></Route>
      <Route path = "/reset-password/:resetToken" exact component = {ResetPassword} /></Switch> :   
        <div className="page-wrapper">
          <Switch>
            <Route exact path="/change-password" component={ChangePassword}></Route>
            <Route path={['/', '/payment']} component={Payment}></Route>
          </Switch>
        </div>
    }
    </div>
  );
}

export default App;
