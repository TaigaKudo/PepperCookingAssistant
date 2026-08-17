export type Stock = {
    stockId: number
    ingredientId: number
    ingredientName: string
    defaultUnit: string
    categoryId: number | null
    categoryName: string | null
    categoryReading: string | null
    userId: number
    quantity: number
    expirationDate: String
}