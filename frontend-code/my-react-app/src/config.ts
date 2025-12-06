// Backend API URL
export const API_BASE_URL = 'http://localhost:8080';

// Use Cognito Authentication (true) or Local Mode (false)
export const USE_COGNITO = true; // Set to true when you have App Client without secret

// AWS Cognito Configuration (only used when USE_COGNITO = true)
export const COGNITO_CONFIG = {
  region: 'eu-west-1',
  userPoolId: 'eu-west-1_zt96SV6xi',
  userPoolClientId: '7qlsrae5tfpa6csvqupibunp6s', // Replace with new client ID (without secret)
};
