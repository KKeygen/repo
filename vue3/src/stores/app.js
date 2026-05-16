import { defineStore } from 'pinia'
import { getCategoryTypes } from '@/api/program'
import { DEFAULT_CITY } from '@/constants/site'

export const useAppStore = defineStore('app', {
  state: () => ({
    currentCity: { ...DEFAULT_CITY },
    categories: []
  }),

  actions: {
    setCity(city) {
      this.currentCity = { ...DEFAULT_CITY, ...city }
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
