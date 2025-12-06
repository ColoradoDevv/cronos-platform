import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="min-h-screen bg-gradient-to-br from-primary/10 via-white to-secondary/10 flex items-center justify-center px-4">
            <div className="max-w-4xl w-full text-center">
                {/* Logo/Title */}
                <div className="mb-12">
                    <h1 className="text-6xl font-bold text-primary mb-4">
                        Cronos Platform
                    </h1>
                    <p className="text-xl text-gray-600">
                        Sistema de Gestión de Citas y Reservas
                    </p>
                </div>

                {/* Description */}
                <div className="mb-12">
                    <p className="text-lg text-gray-700 max-w-2xl mx-auto">
                        Administra tus citas, servicios y clientes de manera eficiente.
                        Inicia sesión para acceder a tu panel de control o regístrate para comenzar.
                    </p>
                </div>

                {/* Buttons */}
                <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                    <Link
                        to="/login"
                        className="w-full sm:w-auto px-8 py-4 bg-primary text-white font-semibold rounded-lg shadow-lg hover:bg-primary/90 transition-all duration-200 transform hover:scale-105"
                    >
                        Iniciar Sesión
                    </Link>
                    <Link
                        to="/register"
                        className="w-full sm:w-auto px-8 py-4 bg-white text-primary font-semibold rounded-lg shadow-lg border-2 border-primary hover:bg-primary/5 transition-all duration-200 transform hover:scale-105"
                    >
                        Registrarse
                    </Link>
                </div>

                {/* Features */}
                <div className="mt-16 grid grid-cols-1 md:grid-cols-3 gap-8">
                    <div className="p-6 bg-white rounded-lg shadow-md">
                        <div className="text-4xl mb-4">📅</div>
                        <h3 className="text-lg font-semibold text-gray-800 mb-2">
                            Gestión de Citas
                        </h3>
                        <p className="text-gray-600">
                            Administra todas tus citas en un solo lugar
                        </p>
                    </div>
                    <div className="p-6 bg-white rounded-lg shadow-md">
                        <div className="text-4xl mb-4">👥</div>
                        <h3 className="text-lg font-semibold text-gray-800 mb-2">
                            Clientes
                        </h3>
                        <p className="text-gray-600">
                            Mantén un registro completo de tus clientes
                        </p>
                    </div>
                    <div className="p-6 bg-white rounded-lg shadow-md">
                        <div className="text-4xl mb-4">⚙️</div>
                        <h3 className="text-lg font-semibold text-gray-800 mb-2">
                            Servicios
                        </h3>
                        <p className="text-gray-600">
                            Configura y gestiona tus servicios
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;
