import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../api/authService';
import ThemeSwitch from '../components/ThemeSwitch';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await authService.login(email, password);
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to login. Please check your credentials.');
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
            <div className="w-full max-w-sm">
                <div className="text-center mb-8">
                    <div className="text-4xl mb-4 text-text hover:animate-spin cursor-default inline-block">
                        <i className="fa-solid fa-hourglass-half"></i>
                    </div>
                    <h2 className="text-2xl font-bold tracking-tight text-text mb-2">
                        Welcome back
                    </h2>
                    <p className="text-sm text-text-secondary">
                        Enter your details to access your workspace.
                    </p>
                </div>

                <div className="notion-card bg-bg border border-border p-8 rounded-lg">
                    <form className="space-y-5" onSubmit={handleSubmit}>
                        {error && (
                            <div className="p-3 bg-red-50 dark:bg-red-900/10 border border-red-100 dark:border-red-900/30 rounded text-sm text-red-600 dark:text-red-400 flex items-center gap-2">
                                <i className="fa-solid fa-circle-exclamation"></i>
                                {error}
                            </div>
                        )}

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
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="block w-full pl-10 pr-3 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
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
                                    type="password"
                                    autoComplete="current-password"
                                    required
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="block w-full pl-10 pr-3 py-2.5 bg-bg border border-border rounded text-sm text-text placeholder-text-muted focus:outline-none focus:border-text focus:ring-1 focus:ring-text transition-all"
                                    placeholder="••••••••"
                                />
                            </div>
                        </div>

                        <div className="flex items-center justify-between">
                            <div className="flex items-center">
                                <input
                                    id="remember-me"
                                    name="remember-me"
                                    type="checkbox"
                                    className="h-4 w-4 text-text border-border rounded bg-bg focus:ring-text"
                                />
                                <label htmlFor="remember-me" className="ml-2 block text-xs text-text-secondary">
                                    Remember me
                                </label>
                            </div>

                            <div className="text-xs">
                                <a href="#" className="font-medium text-text-secondary hover:text-text hover:underline underline-offset-2">
                                    Forgot password?
                                </a>
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full flex items-center justify-center gap-2 py-2.5 px-4 border border-transparent rounded text-sm font-medium bg-text text-bg hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-text transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {loading ? (
                                <i className="fa-solid fa-circle-notch fa-spin"></i>
                            ) : (
                                <>
                                    Sign in
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
                                    Or continue with
                                </span>
                            </div>
                        </div>

                        <div className="mt-6 grid grid-cols-2 gap-3">
                            <button className="w-full inline-flex justify-center py-2 px-4 border border-border rounded bg-bg text-sm font-medium text-text-secondary hover:bg-bg-hover hover:text-text transition-colors">
                                <i className="fa-brands fa-google text-base"></i>
                            </button>
                            <button className="w-full inline-flex justify-center py-2 px-4 border border-border rounded bg-bg text-sm font-medium text-text-secondary hover:bg-bg-hover hover:text-text transition-colors">
                                <i className="fa-brands fa-github text-base"></i>
                            </button>
                        </div>
                    </div>
                </div>

                <p className="mt-8 text-center text-xs text-text-muted">
                    Don't have an account?{' '}
                    <Link to="/register" className="font-medium text-text hover:underline underline-offset-2">
                        Create a workspace
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Login;
