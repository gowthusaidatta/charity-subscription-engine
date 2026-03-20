/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        bg: '#0f172a',
        card: '#111827',
        accent: '#14b8a6',
        accentSoft: '#2dd4bf',
        ink: '#e5e7eb'
      },
      fontFamily: {
        display: ['Poppins', 'sans-serif'],
        body: ['Nunito Sans', 'sans-serif']
      },
      boxShadow: {
        glow: '0 0 0 1px rgba(20, 184, 166, 0.2), 0 20px 40px rgba(2, 6, 23, 0.45)'
      }
    }
  },
  plugins: []
};
