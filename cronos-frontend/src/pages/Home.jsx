import { useState } from 'react';
import { Link } from 'react-router-dom';
import ThemeSwitch from '../components/ThemeSwitch';

const Home = () => {
    // Icons using FontAwesome classes
    const features = [
        {
            icon: 'fa-regular fa-calendar-check',
            title: 'Agenda',
            description: 'Organiza citas sin conflictos. Sincronización total.',
        },
        {
            icon: 'fa-solid fa-users',
            title: 'Clientes',
            description: 'Historial completo y preferencias en un solo lugar.',
        },
        {
            icon: 'fa-regular fa-bell',
            title: 'Recordatorios',
            description: 'Notificaciones automáticas por email y SMS.',
        },
        {
            icon: 'fa-solid fa-chart-line',
            title: 'Analíticas',
            description: 'Métricas claras sobre ocupación e ingresos.',
        },
    ];

    const industries = [
        { icon: 'fa-solid fa-tooth', label: 'Dentistas' },
        { icon: 'fa-solid fa-scissors', label: 'Estilistas' },
        { icon: 'fa-solid fa-person-praying', label: 'Yoga' },
        { icon: 'fa-solid fa-dumbbell', label: 'Fitness' },
        { icon: 'fa-solid fa-stethoscope', label: 'Salud' },
        { icon: 'fa-solid fa-paw', label: 'Veterinaria' },
    ];

    return (
        <div className="min-h-screen bg-bg text-text font-sans selection:bg-accent-blue selection:text-white pb-20">
            {/* Navbar - Minimalist */}
            <nav className="sticky top-0 z-50 bg-bg/95 backdrop-blur-[2px] border-b border-border h-14 flex items-center transition-colors duration-200">
                <div className="wrapper w-full flex items-center justify-between">
                    <div className="flex items-center gap-2 cursor-pointer hover:bg-bg-hover px-2 py-1 rounded transition-colors">
                        <div className="w-5 h-5 bg-text text-bg rounded flex items-center justify-center font-bold text-xs">C</div>
                        <span className="font-semibold text-sm tracking-tight text-text">Cronos</span>
                    </div>

                    <div className="flex items-center gap-2 sm:gap-4">
                        <ThemeSwitch />
                        <div className="h-4 w-px bg-border mx-1"></div>
                        <Link to="/login" className="nav-link hidden sm:block">Log in</Link>
                        <Link to="/register" className="btn-primary">Get Cronos free</Link>
                    </div>
                </div>
            </nav>

            {/* Hero Section - Centered & Stark */}
            <section className="pt-24 pb-16 sm:pt-32 sm:pb-20">
                <div className="wrapper text-center max-w-3xl">
                    <div className="mb-6 text-5xl text-text hover:animate-spin cursor-default inline-block">
                        <i className="fa-solid fa-hourglass-start"></i>
                    </div>

                    <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold mb-6 text-text tracking-tighter leading-tight">
                        La forma simple de <br className="hidden sm:block" />
                        gestionar tu tiempo.
                    </h1>

                    <p className="text-lg sm:text-xl text-text-secondary mb-8 max-w-xl mx-auto leading-relaxed">
                        Cronos organiza tus citas, clientes y negocio. <br className="hidden sm:block" />
                        Potente como una base de datos, simple como una nota.
                    </p>

                    <div className="flex flex-col sm:flex-row gap-3 justify-center items-center mb-12">
                        <Link to="/register" className="btn-primary h-11 px-6 text-[15px]">
                            Empezar gratis
                        </Link>
                        <Link to="/login" className="btn-secondary h-11 px-6 text-[15px]">
                            Ver demo
                        </Link>
                    </div>

                    <div className="text-center">
                        <img src="/cronos-dashboard-mock.png" alt="Dashboard Preview" className="rounded border border-border bg-bg shadow-sm mx-auto max-w-full sm:max-w-2xl opacity-90 hover:opacity-100 transition-opacity" onError={(e) => e.target.style.display = 'none'} />
                    </div>
                </div>
            </section>

            {/* Feature Grid - Notion "Gallery" View */}
            <section className="py-16 border-t border-border">
                <div className="wrapper">
                    <div className="flex items-center gap-2 mb-6 text-text">
                        <i className="fa-regular fa-star text-lg"></i>
                        <h2 className="text-xl font-bold">Todo lo que necesitas</h2>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                        {features.map((feature) => (
                            <div key={feature.title} className="notion-card cursor-default group hover:shadow-hover transition-all duration-200">
                                <div className="text-2xl mb-3 text-text group-hover:scale-110 transition-transform duration-200 origin-top-left">
                                    <i className={feature.icon}></i>
                                </div>
                                <h3 className="text-base font-bold text-text mb-1">
                                    {feature.title}
                                </h3>
                                <p className="text-sm text-text-secondary leading-normal">
                                    {feature.description}
                                </p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Industries - "Tags" Style */}
            <section className="py-16 border-t border-border">
                <div className="wrapper">
                    <div className="flex items-center gap-2 mb-6 text-text">
                        <i className="fa-solid fa-bullseye text-lg"></i>
                        <h2 className="text-xl font-bold">Para todos los equipos</h2>
                    </div>

                    <div className="flex flex-wrap gap-2">
                        {industries.map((item) => (
                            <span key={item.label} className="inline-flex items-center gap-2 px-3 py-1.5 bg-bg-gray border border-border rounded text-sm text-text-secondary hover:bg-bg-hover hover:text-text transition-colors cursor-default">
                                <i className={item.icon}></i>
                                {item.label}
                            </span>
                        ))}
                        <span className="inline-flex items-center gap-2 px-3 py-1.5 bg-bg border border-border-light rounded text-sm text-text-muted italic">
                            + muchos más
                        </span>
                    </div>
                </div>
            </section>

            {/* Footer - Simple List */}
            <footer className="pt-16 pb-8 border-t border-border mt-12 bg-bg-hover/20">
                <div className="wrapper flex flex-col sm:flex-row justify-between items-start sm:items-center gap-8">
                    <div className="flex flex-col gap-2">
                        <div className="flex items-center gap-2">
                            <div className="w-5 h-5 bg-text text-bg rounded flex items-center justify-center font-bold text-[10px]">C</div>
                            <span className="font-semibold text-sm">Cronos</span>
                        </div>
                        <p className="text-xs text-text-muted">© {new Date().getFullYear()} Cronos Platform</p>
                    </div>

                    <div className="flex gap-8 text-sm text-text-secondary">
                        <div className="flex flex-col gap-2">
                            <span className="font-semibold text-text text-xs uppercase tracking-wider text-text-muted">Producto</span>
                            <a href="#" className="hover:text-text hover:underline underline-offset-4">Funcionalidades</a>
                            <a href="#" className="hover:text-text hover:underline underline-offset-4">Precios</a>
                            <a href="#" className="hover:text-text hover:underline underline-offset-4">Changelog</a>
                        </div>
                        <div className="flex flex-col gap-2">
                            <span className="font-semibold text-text text-xs uppercase tracking-wider text-text-muted">Compañía</span>
                            <a href="#" className="hover:text-text hover:underline underline-offset-4">Sobre nosotros</a>
                            <a href="#" className="hover:text-text hover:underline underline-offset-4">Legal</a>
                            <a href="#" className="hover:text-text hover:underline underline-offset-4">Contacto</a>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Home;
