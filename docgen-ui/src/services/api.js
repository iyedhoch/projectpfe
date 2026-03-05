import axios from 'axios';

// Base URL for the Spring Boot backend
const API_BASE_URL = 'http://localhost:8080';

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Fetch all test plans
 * @returns {Promise} - List of all test plans
 */
export const getTestPlans = async () => {
  try {
    const response = await apiClient.get('/api/testplans');
    return response.data;
  } catch (error) {
    console.error('Error fetching test plans:', error);
    throw error;
  }
};

/**
 * Fetch a specific test plan by ID
 * @param {number|string} id - Test plan ID
 * @returns {Promise} - Test plan details
 */
export const getTestPlanById = async (id) => {
  try {
    const response = await apiClient.get(`/api/testplans/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching test plan ${id}:`, error);
    throw error;
  }
};

/**
 * Generate document in specified format
 * @param {number|string} id - Test plan ID
 * @param {string} format - Document format (html, pdf, word, excel)
 * @param {string} template - Template configuration (optional)
 * @returns {Promise} - Generated document data (blob for binary formats)
 */
export const generateDocument = async (id, format, template = null) => {
  try {
    const config = {
      responseType: format === 'html' ? 'text' : 'blob',
    };

    // Add template as query parameter if provided
    if (template) {
      config.params = { template };
    }

    const response = await apiClient.get(
      `/api/testplans/${id}/document/${format}`,
      config
    );

    return response.data;
  } catch (error) {
    console.error(`Error generating ${format} document for test plan ${id}:`, error);
    throw error;
  }
};

/**
 * Get the current active template configuration
 * @returns {Promise} - Template configuration
 */
export const getTemplateConfig = async () => {
  try {
    const response = await apiClient.get('/config');
    return response.data;
  } catch (error) {
    console.error('Error fetching template config:', error);
    throw error;
  }
};

/**
 * Save/update template configuration
 * @param {Object} config - Template configuration object
 * @returns {Promise} - Saved configuration
 */
export const saveTemplateConfig = async (config) => {
  try {
    const response = await apiClient.post('/config', config);
    return response.data;
  } catch (error) {
    console.error('Error saving template config:', error);
    throw error;
  }
};

/**
 * Get version history for a specific test plan
 * @param {number|string} testPlanId - Test plan ID
 * @returns {Promise} - List of versions
 */
export const getVersionHistory = async (testPlanId) => {
  try {
    const response = await apiClient.get(`/versions/${testPlanId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching version history for test plan ${testPlanId}:`, error);
    throw error;
  }
};

// Export the axios instance for custom requests if needed
export default apiClient;
