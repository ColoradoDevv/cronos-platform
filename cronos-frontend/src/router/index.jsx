import { createBrowserRouter } from 'react-router-dom';

const router = createBrowserRouter([
    {
        path: '/',
        element: <div>Home (Public)</div>,
    },
    {
        path: '/login',
        element: <div>Login Page</div>,
    },
    {
        path: '/register',
        element: <div>Register Page</div>,
    },
    {
        path: '/dashboard',
        element: <div>Dashboard (Private)</div>,
    },
]);

export default router;
