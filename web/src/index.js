
import React from 'react';
import ReactDOM from 'react-dom';
import { HashRouter } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import Config from './config.js';

// Config.apiUrl = window.location.origin + '/bncbiller/api';
// Config.apiUrl = 'http://192.168.88.21:8080/bncbiller/api';
Config.apiUrl = 'http://192.168.88.21:8080/psbbiller/api';

ReactDOM.render(
  <HashRouter>
       <App />
  </HashRouter>,
  document.getElementById("root")
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
