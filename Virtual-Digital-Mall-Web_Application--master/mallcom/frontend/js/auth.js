/**
 * Mallcom shared auth helper.
 *
 * Authentication is session-based (a JSESSIONID cookie set by Spring Security),
 * NOT JWT/localStorage-based. This file is the single source of truth the rest
 * of the frontend uses to find out "who is logged in, and what role are they?"
 * by asking the backend's /api/auth/me endpoint on every page load.
 */
const MALLCOM_API_BASE = 'http://localhost:8080';

const MallcomAuth = {
  /**
   * Asks the backend who (if anyone) is currently logged in.
   * Returns the UserResponse object ({id, fullName, email, role}) or null.
   */
  async getCurrentUser() {
    try {
      const res = await fetch(`${MALLCOM_API_BASE}/api/auth/me`, {
        method: 'GET',
        credentials: 'include' // send the session cookie
      });
      if (!res.ok) return null;
      const body = await res.json();
      return body.data || null;
    } catch (err) {
      return null;
    }
  },

  async logout() {
    try {
      await fetch(`${MALLCOM_API_BASE}/api/auth/logout`, {
        method: 'POST',
        credentials: 'include'
      });
    } finally {
      window.location.href = 'login.html';
    }
  },

  /**
   * Wires up any element with id="navAuthLink" (Login/Logout) and any elements
   * marked data-role="ADMIN" (hidden unless the current user is an admin) or
   * data-role="USER" (hidden for guests, shown for USER and ADMIN alike).
   * Call this on every page that has a navbar.
   */
  async renderNav() {
    const user = await this.getCurrentUser();

    const authLink = document.getElementById('navAuthLink');
    if (authLink) {
      if (user) {
        authLink.textContent = 'Logout (' + user.fullName.split(' ')[0] + ')';
        authLink.href = '#';
        authLink.onclick = (e) => { e.preventDefault(); MallcomAuth.logout(); };
      } else {
        authLink.textContent = 'Login';
        authLink.href = 'login.html';
        authLink.onclick = null;
      }
    }

    // Hide admin-only nav entries (e.g. "Dashboard") from everyone except ADMIN
    document.querySelectorAll('[data-role="ADMIN"]').forEach(el => {
      el.style.display = (user && user.role === 'ADMIN') ? '' : 'none';
    });

    // Hide user-only nav entries (e.g. "Products", "Profile") from guests
    document.querySelectorAll('[data-role="USER"]').forEach(el => {
      el.style.display = user ? '' : 'none';
    });

    return user;
  },

  /**
   * Redirects to login.html if nobody is logged in, or shows a 403 message
   * and redirects home if the logged-in user isn't an ADMIN. Call this at the
   * top of admin-only pages (e.g. admin-dashboard.html).
   */
  async requireAdmin() {
    const user = await this.getCurrentUser();
    if (!user) {
      window.location.href = 'login.html';
      return null;
    }
    if (user.role !== 'ADMIN') {
      alert('Access Denied: this page is for administrators only.');
      window.location.href = 'Minor_project.html';
      return null;
    }
    return user;
  },

  /** Redirects to login.html if nobody is logged in. Call on USER-facing pages. */
  async requireLogin() {
    const user = await this.getCurrentUser();
    if (!user) {
      window.location.href = 'login.html';
      return null;
    }
    return user;
  }
};
