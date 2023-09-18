import React from 'react';
import Payment from './components/payment';
import Register from './components/register';

const routes = [
    {
        path : ['/', '/payment'],
        exact : true,
        component : () => <Payment />
    },

    {
        path : ['/', '/register'],
        exact : true,
        component : () => <Register />
    }
];

export default routes;