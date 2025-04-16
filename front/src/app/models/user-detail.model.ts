import { BasicUserInfo } from './basic-user-info.model';
import { Competence } from './competence.model';

export interface UserDetail extends BasicUserInfo {
  competences: Competence[];
} 