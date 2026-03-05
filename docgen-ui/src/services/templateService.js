import apiClient from './api';

/**
 * Get the currently active template configuration
 * @returns {Promise} - Active template configuration
 */
export const getActiveTemplate = async () => {
  try {
    const response = await apiClient.get('/config');
    return response.data;
  } catch (error) {
    console.error('Error fetching active template:', error);
    throw error;
  }
};

/**
 * Get all available templates
 * @returns {Promise} - Array of all template configurations
 */
export const getAllTemplates = async () => {
  try {
    const response = await apiClient.get('/config/all');
    return response.data;
  } catch (error) {
    console.error('Error fetching all templates:', error);
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
