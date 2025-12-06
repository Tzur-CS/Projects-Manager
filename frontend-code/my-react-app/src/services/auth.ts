import { Amplify } from 'aws-amplify';
import { signIn, signOut, getCurrentUser, fetchAuthSession } from 'aws-amplify/auth';
import { COGNITO_CONFIG, USE_COGNITO } from '../config';

// Configure Amplify only if using Cognito
if (USE_COGNITO) {
  Amplify.configure({
    Auth: {
      Cognito: {
        userPoolId: COGNITO_CONFIG.userPoolId,
        userPoolClientId: COGNITO_CONFIG.userPoolClientId,
      }
    }
  });
}

// Local mode - no Cognito
const localAuth = {
  async signIn(username: string, password: string) {
    // Simple validation for local testing
    if (password.length >= 4) {
      const user = { username };
      localStorage.setItem('local_user', JSON.stringify(user));
      return user;
    }
    throw new Error('Password must be at least 4 characters');
  },

  async signOut() {
    localStorage.removeItem('local_user');
  },

  async getCurrentUser() {
    const stored = localStorage.getItem('local_user');
    return stored ? JSON.parse(stored) : null;
  },

  async isAuthenticated() {
    return localStorage.getItem('local_user') !== null;
  },

  async getAccessToken() {
    return null; // No token in local mode
  },
};

export const authService = {
  // Sign in with username and password
  async signIn(username: string, password: string) {
    if (!USE_COGNITO) {
      return await localAuth.signIn(username, password);
    }

    try {
      const result = await signIn({ username, password });
      console.log('=== COGNITO LOGIN SUCCESS ===');
      console.log('Sign in result:', result);
      return result;
    } catch (error) {
      console.error('Cognito sign in error:', error);
      throw error;
    }
  },

  // Sign out
  async signOut() {
    if (!USE_COGNITO) {
      return await localAuth.signOut();
    }

    try {
      await signOut();
      console.log('Signed out successfully');
    } catch (error) {
      console.error('Sign out error:', error);
      throw error;
    }
  },

  // Get current authenticated user
  async getCurrentUser() {
    if (!USE_COGNITO) {
      return await localAuth.getCurrentUser();
    }

    try {
      const user = await getCurrentUser();
      return user;
    } catch {
      return null;
    }
  },

  // Check if user is authenticated
  async isAuthenticated() {
    if (!USE_COGNITO) {
      return await localAuth.isAuthenticated();
    }

    try {
      await getCurrentUser();
      return true;
    } catch {
      return false;
    }
  },

  // Get access token for API calls
  async getAccessToken(): Promise<string | null> {
    if (!USE_COGNITO) {
      return await localAuth.getAccessToken();
    }

    try {
      const session = await fetchAuthSession();
      const token = session.tokens?.accessToken?.toString();
      console.log('=== ACCESS TOKEN ===');
      console.log(token);
      console.log('====================');
      return token || null;
    } catch (error) {
      console.error('Error getting access token:', error);
      return null;
    }
  },

  // Get ID token for API calls
  async getIdToken(): Promise<string | null> {
    if (!USE_COGNITO) {
      return null;
    }

    try {
      const session = await fetchAuthSession();
      const token = session.tokens?.idToken?.toString();
      return token || null;
    } catch (error) {
      console.error('Error getting ID token:', error);
      return null;
    }
  }
};

export default authService;
