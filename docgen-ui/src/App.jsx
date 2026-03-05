import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import Templates from './pages/Templates';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <nav className="app-nav">
          <div className="app-nav__container">
            <h2 className="app-nav__brand">DocGen Back-Office</h2>
            <ul className="app-nav__links">
              <li>
                <Link to="/templates" className="app-nav__link">
                  📄 Templates
                </Link>
              </li>
              <li>
                <Link to="/dashboard" className="app-nav__link">
                  📊 Dashboard
                </Link>
              </li>
              <li>
                <Link to="/export" className="app-nav__link">
                  📥 Export
                </Link>
              </li>
            </ul>
          </div>
        </nav>

        <main className="app-main">
          <Routes>
            <Route path="/" element={<Navigate to="/templates" replace />} />
            <Route path="/templates" element={<Templates />} />
            <Route path="/dashboard" element={<PlaceholderPage title="Dashboard" />} />
            <Route path="/export" element={<PlaceholderPage title="Export" />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

// Placeholder component for pages not yet implemented
function PlaceholderPage({ title }) {
  return (
    <div style={{ padding: '40px', textAlign: 'center' }}>
      <h1>{title}</h1>
      <p>This page is coming soon...</p>
    </div>
  );
}

export default App;
