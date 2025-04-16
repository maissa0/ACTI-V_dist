export interface BasicUserInfo {
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    twoFactorEnabled?: boolean;
} 