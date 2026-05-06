import { defineStore } from 'pinia'
import { getCategoryTypes } from '@/api/program'

export const useAppStore = defineStore('app', {
  state: () => ({
    currentCity: {
      id: '',
      name: '全国'
    },
    categories: []
  }),

  actions: {
    setCity(city) {
      this.currentCity = { ...city }
    },

    async loadCategories() {
      try {
        const res = await getCategoryTypes({ type: 1 })
        this.categories = res.data || []
      } catch (e) {
        console.error('Failed to load categories:', e)
      }
    }
  }
})
