import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICV, NewCV } from '../cv.model';

export type PartialUpdateCV = Partial<ICV> & Pick<ICV, 'id'>;

type RestOf<T extends ICV | NewCV> = Omit<T, 'dateUpload'> & {
  dateUpload?: string | null;
};

export type RestCV = RestOf<ICV>;

export type NewRestCV = RestOf<NewCV>;

export type PartialUpdateRestCV = RestOf<PartialUpdateCV>;

export type EntityResponseType = HttpResponse<ICV>;
export type EntityArrayResponseType = HttpResponse<ICV[]>;

@Injectable({ providedIn: 'root' })
export class CVService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cvs');

  create(cV: NewCV): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cV);
    return this.http.post<RestCV>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cV: ICV): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cV);
    return this.http
      .put<RestCV>(`${this.resourceUrl}/${this.getCVIdentifier(cV)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cV: PartialUpdateCV): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cV);
    return this.http
      .patch<RestCV>(`${this.resourceUrl}/${this.getCVIdentifier(cV)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCV>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCV[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCVIdentifier(cV: Pick<ICV, 'id'>): number {
    return cV.id;
  }

  compareCV(o1: Pick<ICV, 'id'> | null, o2: Pick<ICV, 'id'> | null): boolean {
    return o1 && o2 ? this.getCVIdentifier(o1) === this.getCVIdentifier(o2) : o1 === o2;
  }

  addCVToCollectionIfMissing<Type extends Pick<ICV, 'id'>>(cVCollection: Type[], ...cVSToCheck: (Type | null | undefined)[]): Type[] {
    const cVS: Type[] = cVSToCheck.filter(isPresent);
    if (cVS.length > 0) {
      const cVCollectionIdentifiers = cVCollection.map(cVItem => this.getCVIdentifier(cVItem));
      const cVSToAdd = cVS.filter(cVItem => {
        const cVIdentifier = this.getCVIdentifier(cVItem);
        if (cVCollectionIdentifiers.includes(cVIdentifier)) {
          return false;
        }
        cVCollectionIdentifiers.push(cVIdentifier);
        return true;
      });
      return [...cVSToAdd, ...cVCollection];
    }
    return cVCollection;
  }

  protected convertDateFromClient<T extends ICV | NewCV | PartialUpdateCV>(cV: T): RestOf<T> {
    return {
      ...cV,
      dateUpload: cV.dateUpload?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCV: RestCV): ICV {
    return {
      ...restCV,
      dateUpload: restCV.dateUpload ? dayjs(restCV.dateUpload) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCV>): HttpResponse<ICV> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCV[]>): HttpResponse<ICV[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
