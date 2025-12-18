import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../api/authService';
import PasswordStrengthIndicator from '../components/PasswordStrengthIndicator';
import ThemeSwitch from '../components/ThemeSwitch';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError("Passwords don't match");
            return;
        }

        setLoading(true);

        try {
            await authService.register({
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                password: formData.password
            });
            // User is now auto-logged in, redirect to dashboard
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to register. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-bg text-text p-4 selection:bg-accent-blue selection:text-white relative">

            {/* Minimalist Top Nav */}
            <div className="absolute top-0 left-0 right-0 p-6 flex justify-between items-center w-full max-w-6xl mx-auto">
                <Link to="/" className="flex items-center gap-2 group">
                    <div className="w-5 h-5 bg-text text-bg rounded flex items-center justify-center font-bold text-xs group-hover:scale-105 transition-transform">C</div>
                    <span className="font-semibold text-sm tracking-tight text-text">Cronos</span>
                </Link>
                <ThemeSwitch />
            </div>

            {/* Central Notion-Style Card */}
            <div className="w-full max-w-sm mt-12 sm:mt-0">
                <div className="text-center mb-4">
                    {/* <div className="text-4xl mb-4 text-text hover:animate-spin cursor-default inline-block">
                        <i className="fa-solid fa-hourglass-half"></i>
                    </div> */}
                    <h2 className="text-2x1 font-bold tracking-tight text-text mb-1">
                        Create an account
                    </h2>
                    {/* <p className="text-sm text-text-secondary">
                        Start your journey with Cronos today.
                    </p> */}
                </div>

                <div className="notion-card bg-bg border border-border p-8 rounded-lg">
                    <form className="space-y-4" onSubmit={handleSubmit}>
                        {error && (
                            <div className="p-3 bg-red-50 dark:bg-red-900/10 border border-red-100 dark:border-red-900/30 rounded text-sm text-red-600 dark:text-red-400 flex items-center gap-2">
                                <i className="fa-solid fa-circle-exclamation"></i>
                                {error}
                            </div>
                        )}

                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-1.5">
                                <label htmlFor="firstName" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary">
                                    First Name
                                </label>
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-text-muted">
                                        <i className="fa-solid fa-user"></i>
                                    </div>
                                    <input
                                        id="firstName"
                                        name="firstName"
                                        type="text"
                                        autoComplete="given-name"
                                        required
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        className="block w-full pl-9 pr-3 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
                                        placeholder="John"
                                    />
                                </div>
                            </div>
                            <div className="space-y-1.5">
                                <label htmlFor="lastName" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary">
                                    Last Name
                                </label>
                                <div className="relative">
                                    <input
                                        id="lastName"
                                        name="lastName"
                                        type="text"
                                        autoComplete="family-name"
                                        required
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        className="block w-full px-3 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
                                        placeholder="Doe"
                                    />
                                </div>
                            </div>
                        </div>

                        <div className="space-y-1.5">
                            <label htmlFor="email" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary">
                                Email
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-text-muted">
                                    <i className="fa-regular fa-envelope"></i>
                                </div>
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    autoComplete="email"
                                    required
                                    value={formData.email}
                                    onChange={handleChange}
                                    className="block w-full pl-9 pr-3 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
                                    placeholder="name@company.com"
                                />
                            </div>
                        </div>

                        <div className="space-y-1.5">
                            <label htmlFor="password" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary">
                                Password
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-text-muted">
                                    <i className="fa-solid fa-lock"></i>
                                </div>
                                <input
                                    id="password"
                                    name="password"
                                    type={showPassword ? "text" : "password"}
                                    autoComplete="new-password"
                                    required
                                    value={formData.password}
                                    onChange={handleChange}
                                    className="block w-full pl-9 pr-10 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
                                    placeholder="••••••••"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute inset-y-0 right-0 pr-3 flex items-center text-text-muted hover:text-text"
                                >
                                    <i className={`fa-solid ${showPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
                                </button>
                            </div>
                            <PasswordStrengthIndicator password={formData.password} />
                        </div>

                        <div className="space-y-1.5">
                            <label htmlFor="confirmPassword" className="block text-xs font-semibold uppercase tracking-wider text-text-secondary">
                                Confirm Password
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-text-muted">
                                    <i className="fa-solid fa-lock"></i>
                                </div>
                                <input
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    type={showConfirmPassword ? "text" : "password"}
                                    autoComplete="new-password"
                                    required
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                    className="block w-full pl-9 pr-10 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
                                    placeholder="••••••••"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    className="absolute inset-y-0 right-0 pr-3 flex items-center text-text-muted hover:text-text"
                                >
                                    <i className={`fa-solid ${showConfirmPassword ? 'fa-eye-slash' : 'fa-eye'}`}></i>
                                </button>
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full flex items-center justify-center gap-2 py-3 px-4 mt-2 border border-transparent rounded text-sm font-medium bg-text text-bg hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-text transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {loading ? (
                                <i className="fa-solid fa-circle-notch fa-spin"></i>
                            ) : (
                                <>
                                    Create account
                                    <i className="fa-solid fa-arrow-right text-xs mt-0.5 opacity-70"></i>
                                </>
                            )}
                        </button>
                    </form>

                    <div className="mt-6">
                        <div className="relative">
                            <div className="absolute inset-0 flex items-center">
                                <div className="w-full border-t border-border"></div>
                            </div>
                            <div className="relative flex justify-center text-xs">
                                <span className="px-2 bg-bg text-text-muted">
                                    Already have an account?
                                </span>
                            </div>
                        </div>

                        <div className="mt-6">
                            <Link
                                to="/login"
                                className="w-full inline-flex justify-center py-2.5 px-4 border border-border rounded bg-bg text-sm font-medium text-text-secondary hover:bg-bg-hover hover:text-text transition-colors"
                            >
                                Sign in
                            </Link>
                        </div>
                    </div>
                </div>

                <div className="mt-8 text-center text-xs text-text-muted">
                    By creating an account, you agree to our{' '}
                    <a href="#" className="font-medium text-text hover:underline underline-offset-2">Terms</a>
                    {' '}and{' '}
                    <a href="#" className="font-medium text-text hover:underline underline-offset-2">Privacy Policy</a>
                </div>
            </div>
        </div>
    );
};

export default Register;
