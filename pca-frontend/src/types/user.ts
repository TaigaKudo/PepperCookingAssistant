export type User = {
    id: Number
    name: string
    email: string
}

export type EmailChangeRequest = {
    newEmail: string
    currentPassword: string
}