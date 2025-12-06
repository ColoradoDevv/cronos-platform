import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Home from '../pages/Home';
import ClientLayout from '../layouts/ClientLayout';

const PublicLayout = () => {
    const token = localStorage.getItem('token');
    if (token) {
        return <Navigate to="/dashboard" replace />;
    }
    return <Outlet />;
};

const PrivateLayout = () => {
    const token = localStorage.getItem('token');
    if (!token) {
        return <Navigate to="/login" replace />;
    }
    return <Outlet />;
};

const router = createBrowserRouter([
    {
        element: <PublicLayout />,
        children: [
            {
                path: '/',
                element: <Home />,
            },
            {
                path: '/login',
                element: <Login />,
            },
            {
                path: '/register',
                element: <Register />,
            },
        ],
    },
    {
        path: '/book/:slug',
        element: <ClientLayout />,
        children: [
            {
                index: true,
                element: <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
                    <h2 className="text-3xl font-extrabold text-gray-900">Book an Appointment</h2>
                    <p className="mt-4 text-lg text-gray-500">Select a service to get started.</p>
                </div>,
            },
        ],
    },
    {
        element: <PrivateLayout />,
        children: [
            {
                path: '/dashboard',
                element: <div>Dashboard (Private)</div>,
            },
        ],
    },
]);

export default router;
