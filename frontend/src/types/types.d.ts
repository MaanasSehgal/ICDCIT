export interface Step {
  icon: React.ReactNode;
  title: string;
  description: string;
  number: number;
}

export interface StepCardProps {
  step: Step;
}

export interface Feature {
  icon: React.ReactNode;
  title: string;
  description: string;
  highlight?: string;
}

export interface AuthData {
  token: string | null;
  userId: number | null;
  doctorId: number | null;
  role: string | null;
  userName: string | null;
}

export interface Community {
  id: number;
  name: string;
  image: string;
  summary: string;
  members: {
    id: number;
    name: string;
    role: string;
    avatar: string;
  }[];
  messages: {
    id: number;
    userId: number;
    userName: string;
    userAvatar: string;
    content: string;
    timestamp: string;
    isImage: boolean;
  }[];
}

export interface Message {
  id: number;
  userId: number;
  userName: string;
  userAvatar: string;
  content: string;
  timestamp: string;
  isImage: boolean;
}

export interface Doctor {
    id: string;
    name: string;
    bio: string;
    profilePhoto: string;
    proficiencies: string[];
    rating: number;
    experience: number;
    nextAvailable: string;
    consultationFee: number;
    location: string;
}

export interface NewsArticle {
    title: string;
    description: string;
    content: string;
    url: string;
    image: string;
    publishedAt: string;
    source: {
        name: string;
        url: string;
    };
}