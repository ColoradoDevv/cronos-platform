import React from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../api/authService';
import { LogOut, Home as HomeIcon, Calendar, Users, Settings } from 'lucide-react';

const Dashboard = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        authService.logout();
    };

    const handleGoHome = () => {
        // Remove token to access home
        localStorage.removeItem('token');
        navigate('/');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header/Navbar */}
            <nav className="bg-white shadow-sm border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        {/* Logo/Brand */}
                        <div className="flex items-center">
                            <h1 className="text-2xl font-bold text-primary">Cronos Platform</h1>
                        </div>

                        {/* Navigation Links */}
                        <div className="hidden md:flex items-center space-x-4">
                            <button
                                onClick={handleGoHome}
                                className="flex items-center gap-2 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100 transition-colors"
                            >
                                <HomeIcon className="h-5 w-5" />
                                <span>Home</span>
                            </button>
                            <button className="flex items-center gap-2 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100 transition-colors">
                                <Calendar className="h-5 w-5" />
                                <span>Appointments</span>
                            </button>
                            <button className="flex items-center gap-2 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100 transition-colors">
                                <Users className="h-5 w-5" />
                                <span>Clients</span>
                            </button>
                            <button className="flex items-center gap-2 px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100 transition-colors">
                                <Settings className="h-5 w-5" />
                                <span>Settings</span>
                            </button>
                        </div>

                        {/* Logout Button */}
                        <button
                            onClick={handleLogout}
                            className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors shadow-sm"
                        >
                            <LogOut className="h-5 w-5" />
                            <span>Logout</span>
                        </button>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="mb-8">
                    <h2 className="text-3xl font-bold text-gray-900">Dashboard</h2>
                    <p className="mt-2 text-gray-600">Welcome back! Here's an overview of your account.</p>
                </div>

                {/* Dashboard Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                    {/* Stat Card 1 */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-600">Total Appointments</p>
                                <p className="text-3xl font-bold text-gray-900 mt-2">0</p>
                            </div>
                            <Calendar className="h-12 w-12 text-primary opacity-20" />
                        </div>
                    </div>

                    {/* Stat Card 2 */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-600">Active Clients</p>
                                <p className="text-3xl font-bold text-gray-900 mt-2">0</p>
                            </div>
                            <Users className="h-12 w-12 text-primary opacity-20" />
                        </div>
                    </div>

                    {/* Stat Card 3 */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-600">Services</p>
                                <p className="text-3xl font-bold text-gray-900 mt-2">0</p>
                            </div>
                            <Settings className="h-12 w-12 text-primary opacity-20" />
                        </div>
                    </div>

                    {/* Stat Card 4 */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-600">Revenue</p>
                                <p className="text-3xl font-bold text-gray-900 mt-2">$0</p>
                            </div>
                            <div className="text-4xl">💰</div>
                        </div>
                    </div>
                </div>

                {/* Recent Activity */}
                <div className="bg-white rounded-lg shadow">
                    <div className="px-6 py-4 border-b border-gray-200">
                        <h3 className="text-lg font-semibold text-gray-900">Recent Activity</h3>
                    </div>
                    <div className="px-6 py-8 text-center text-gray-500">
                        <p>No recent activity to display</p>
                        <p className="text-sm mt-2">Your appointments and bookings will appear here</p>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default Dashboard;
