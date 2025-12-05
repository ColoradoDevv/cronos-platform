import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';

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
                element: <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                    <h1 className="text-4xl font-bold text-primary">Cronos Platform</h1>
                </div>,
            },
            {
                path: '/login',
                element: <div>Login Page</div>,
            },
            {
                path: '/register',
                element: <div>Register Page</div>,
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
