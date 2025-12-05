import api from './axios';

const tenantService = {
    getPublicTenantBySlug: async (slug) => {
        const response = await api.get(`/public/tenants/${slug}`);
        return response.data;
    }
};

export default tenantService;
