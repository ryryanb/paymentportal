import React, { useState, useEffect } from 'react';
import { Row, Col, Form, Button, Table } from 'react-bootstrap';
import DatePicker from "react-datepicker";
import { CSVLink } from "react-csv";
import Config from '../config.js';
import moment from 'moment';
import useToken from '../useToken';
import utils from '../utils';
import cogoToast from 'cogo-toast';

function getDate(date) {
  if(date && date !== '') {
    return moment(date).format('MM/DD/YYYY');
  }
  return date;
}
export default function Payment() {
  const [billAccountNumber, setBillAccountNumber] = useState('');
  const [fromDate, setFromDate] = useState(new Date());
  const [toDate, setToDate] = useState(new Date());
  const [error, setError] = useState('');
  const { token } = useToken();
  const [payments, setPayments] = useState([]);
  const [totalQuantity, setTotalQuantity] = useState();
  const [totalAmount, setTotalAmount] = useState({});
  const paymentHeader = ["Biller ID", "Transaction ID", "From Account", "To Account", "Amount", "Transaction Date", "Comment"];
  const csvHeaders = [
    { label: "Biller ID", key: "billerId" },
    { label: "Transaction ID", key: "fromAccount" },
    { label: "From Account", key: "fromAccount" },
    { label: "To Account", key: "toAccount" },
    { label: "Amount", key: "amount" },
    { label: "Transaction Date", key: "date" },
    { label: "Comment", key: "comment" }
  ];

  const getPayments = async () => {
    setError('');
    let billSearch = {
      billAccountNumber: billAccountNumber,
      fromDate: getDate(fromDate),
      toDate: getDate(toDate)
    }

    fetch(Config.apiUrl + '/payment/history', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(billSearch)
    }).then(response => {
      return response.json()
    })
      .then(data => {
        if(data.code === 200) {
          setPayments(data.details.rows);
          setTotalQuantity(data.details.total);
          setTotalAmount(data.details.totalAmount);
        } else if (data.status && data.status === 401) {
          utils.logout();
        }
      })
      .catch(err => {
        if (err.text) {
          err.text().then(errorMessage => {
            setError(errorMessage);
          })
        } else {
          setError('payment Error');
        }
      })
  }

  const search = async e => {
    e.preventDefault();
    getPayments();
  }

  useEffect(() => {
    getPayments();
  }, []);

  const handleDateChange = (name, date) => {
    if(name === 'toDate') {
      if(date < fromDate) {
        cogoToast.error('To Date should always be greater than equal to From Date.', { position: 'top-center', heading: 'Error' });
      } else {
        setToDate(date);
      }
    } else {
      setFromDate(date);
    }
  };

  return (
    <div id="payment-history" className="container p-2">
      <div id="filter" className="pb-2">
        <Form onSubmit={search}>
          <Row>
            <Col>
              <Form.Group>
                <Form.Label>Bill Account Number</Form.Label>
                <Form.Control type="text" name="billAccountNumber" value={billAccountNumber} onChange={e => setBillAccountNumber(e.target.value)} />
              </Form.Group>
            </Col>
            <Col>
              <Form.Group>
                <Form.Label>From Date</Form.Label>
                <DatePicker id="fromDate" selected={fromDate} dateFormat="MM/dd/yyyy" onChange={handleDateChange.bind(this, 'fromDate')} maxDate={
                    new Date()
                  } />
              </Form.Group>
            </Col>
            <Col>
              <Form.Group>
                <Form.Label>To Date</Form.Label>
                <DatePicker id="toDate" selected={toDate} dateFormat="MM/dd/yyyy" onChange={handleDateChange.bind(this, 'toDate')} maxDate={
                    new Date()
                  } />
              </Form.Group>
            </Col>
          </Row>
          {error !== '' && (<div className="text-danger">{error}</div>)}
          <Button variant="primary" type="submit">
            Search
              </Button>
        </Form>
      </div>
      <div className="row">
        <div className="col">
          { payments.length > 0 &&
          <div>
            <CSVLink className="btn btn-success" data={payments} headers={csvHeaders} filename={"Bill Payments.csv"}>
              Export CSV
            </CSVLink>
            <div className="float-right">
                Total number of transactions: <strong>{totalQuantity} </strong><br/>
                Total amount: <strong>{Object.keys(totalAmount).map((key, i) => (
                  <span key={i}>{key} {totalAmount[key]} {i < (Object.keys(totalAmount).length - 1) && "; "}</span>
                ))}</strong>
            </div>
          </div>
          }
          <Table striped bordered hover>
            <thead>
              <tr>
                {paymentHeader.map((value, index) => (
                  <th className="text-center" key={index}>
                    <div>{value}</div>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {payments.length > 0 ?
              payments.map(payment => (
                <tr key={payment.id}>
                  <td className="text-center">{payment.billerId}</td>
                  <td className="text-center">{payment.transactionId}</td>
                  <td className="text-center">{payment.fromAccount}</td>
                  <td className="text-center">{payment.toAccount}</td>
                  <td className="text-right">{payment.currency} {payment.amount}</td>
                  <td className="text-center">{payment.date}</td>
                  <td>{payment.comment}</td>
                </tr>
              )) : <tr><td className="text-center" colSpan="7">Bill Payments list is empty</td></tr>}
            </tbody>
          </Table>
          { payments.length > 0 &&
          <div className="text-right">
              Total number of transactions: <strong>{totalQuantity} </strong><br/>
              Total amount: <strong>{Object.keys(totalAmount).map((key, i) => (
                  <span key={i}>{key} {totalAmount[key]} {i < (Object.keys(totalAmount).length - 1) && "; "}</span>
                ))}</strong>
          </div>
          }
        </div>
      </div>
    </div>
  );
}