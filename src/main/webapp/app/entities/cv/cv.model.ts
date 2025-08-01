import dayjs from 'dayjs/esm';

export interface ICV {
  id: number;
  urlFichier?: string | null;
  nomFichier?: string | null;
  dateUpload?: dayjs.Dayjs | null;
}

export type NewCV = Omit<ICV, 'id'> & { id: null };
