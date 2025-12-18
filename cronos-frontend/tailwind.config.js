/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    DEFAULT: 'var(--color-primary)',
                    hover: 'var(--color-primary-hover)',
                    light: 'var(--color-primary-light)',
                    subtle: 'var(--color-primary-subtle)',
                },
                accent: {
                    orange: 'var(--color-accent-orange)',
                    yellow: 'var(--color-accent-yellow)',
                    green: 'var(--color-accent-green)',
                    purple: 'var(--color-accent-purple)',
                },
                text: {
                    DEFAULT: 'var(--color-text)',
                    secondary: 'var(--color-text-secondary)',
                    muted: 'var(--color-text-muted)',
                },
                border: {
                    DEFAULT: 'var(--color-border)',
                    light: 'var(--color-border-light)',
                },
                bg: {
                    DEFAULT: 'var(--color-bg)',
                    warm: 'var(--color-bg-warm)',
                    card: 'var(--color-bg-card)',
                    'card-hover': 'var(--color-bg-card-hover)',
                },
            },
            fontFamily: {
                sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'sans-serif'],
            },
            borderRadius: {
                'xl': '12px',
                '2xl': '16px',
                '3xl': '20px',
            },
            boxShadow: {
                'card': 'var(--shadow-card)',
                'card-hover': 'var(--shadow-card-hover)',
            },
            animation: {
                'float': 'float 6s ease-in-out infinite',
                'pulse-ring': 'pulse-ring 3s ease-in-out infinite',
                'fade-in-up': 'fadeInUp 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards',
            },
        },
    },
    plugins: [],
}
