export type Ingredient = {
    id: number
    name: string
    reading: string
    defaultUnit: string
    categoryId: number | null
    categoryName: string | null
    categoryReading: string | null
}