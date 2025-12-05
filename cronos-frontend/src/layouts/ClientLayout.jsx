import React, { useEffect, useState } from 'react';
import { Outlet, useParams } from 'react-router-dom';
import tenantService from '../api/tenantService';

const ClientLayout = () => {
    const { slug } = useParams();
    const [tenant, setTenant] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTenant = async () => {
            try {
                const data = await tenantService.getPublicTenantBySlug(slug);
                setTenant(data);

                // Dynamically set primary color
                if (data.primaryColor) {
                    document.documentElement.style.setProperty('--primary-color', data.primaryColor);
                }
            } catch (err) {
                console.error("Failed to load tenant", err);
                setError("Business not found");
            } finally {
                setLoading(false);
            }
        };

        if (slug) {
            fetchTenant();
        }
    }, [slug]);

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="text-center">
                    <h1 className="text-4xl font-bold text-gray-900 mb-4">404</h1>
                    <p className="text-xl text-gray-600">{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Public Navbar */}
            <nav className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex">
                            <div className="flex-shrink-0 flex items-center">
                                {tenant.logoUrl ? (
                                    <img
                                        className="h-8 w-auto"
                                        src={tenant.logoUrl}
                                        alt={tenant.name}
                                    />
                                ) : (
                                    <span className="text-xl font-bold text-primary">{tenant.name}</span>
                                )}
                            </div>
                        </div>
                        <div className="flex items-center">
                            <button className="bg-primary text-white px-4 py-2 rounded-md text-sm font-medium hover:opacity-90 transition-opacity">
                                Book Now
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <main>
                <Outlet context={{ tenant }} />
            </main>
        </div>
    );
};

export default ClientLayout;
