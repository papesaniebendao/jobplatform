import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOffreEmploi, NewOffreEmploi } from '../offre-emploi.model';

export type PartialUpdateOffreEmploi = Partial<IOffreEmploi> & Pick<IOffreEmploi, 'id'>;

type RestOf<T extends IOffreEmploi | NewOffreEmploi> = Omit<T, 'datePublication' | 'dateExpiration'> & {
  datePublication?: string | null;
  dateExpiration?: string | null;
};

export type RestOffreEmploi = RestOf<IOffreEmploi>;

export type NewRestOffreEmploi = RestOf<NewOffreEmploi>;

export type PartialUpdateRestOffreEmploi = RestOf<PartialUpdateOffreEmploi>;

export type EntityResponseType = HttpResponse<IOffreEmploi>;
export type EntityArrayResponseType = HttpResponse<IOffreEmploi[]>;

@Injectable({ providedIn: 'root' })
export class OffreEmploiService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/offre-emplois');

  create(offreEmploi: NewOffreEmploi): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(offreEmploi);
    return this.http
      .post<RestOffreEmploi>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(offreEmploi: IOffreEmploi): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(offreEmploi);
    return this.http
      .put<RestOffreEmploi>(`${this.resourceUrl}/${this.getOffreEmploiIdentifier(offreEmploi)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(offreEmploi: PartialUpdateOffreEmploi): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(offreEmploi);
    return this.http
      .patch<RestOffreEmploi>(`${this.resourceUrl}/${this.getOffreEmploiIdentifier(offreEmploi)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOffreEmploi>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOffreEmploi[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOffreEmploiIdentifier(offreEmploi: Pick<IOffreEmploi, 'id'>): number {
    return offreEmploi.id;
  }

  compareOffreEmploi(o1: Pick<IOffreEmploi, 'id'> | null, o2: Pick<IOffreEmploi, 'id'> | null): boolean {
    return o1 && o2 ? this.getOffreEmploiIdentifier(o1) === this.getOffreEmploiIdentifier(o2) : o1 === o2;
  }

  addOffreEmploiToCollectionIfMissing<Type extends Pick<IOffreEmploi, 'id'>>(
    offreEmploiCollection: Type[],
    ...offreEmploisToCheck: (Type | null | undefined)[]
  ): Type[] {
    const offreEmplois: Type[] = offreEmploisToCheck.filter(isPresent);
    if (offreEmplois.length > 0) {
      const offreEmploiCollectionIdentifiers = offreEmploiCollection.map(offreEmploiItem => this.getOffreEmploiIdentifier(offreEmploiItem));
      const offreEmploisToAdd = offreEmplois.filter(offreEmploiItem => {
        const offreEmploiIdentifier = this.getOffreEmploiIdentifier(offreEmploiItem);
        if (offreEmploiCollectionIdentifiers.includes(offreEmploiIdentifier)) {
          return false;
        }
        offreEmploiCollectionIdentifiers.push(offreEmploiIdentifier);
        return true;
      });
      return [...offreEmploisToAdd, ...offreEmploiCollection];
    }
    return offreEmploiCollection;
  }

  protected convertDateFromClient<T extends IOffreEmploi | NewOffreEmploi | PartialUpdateOffreEmploi>(offreEmploi: T): RestOf<T> {
    return {
      ...offreEmploi,
      datePublication: offreEmploi.datePublication?.toJSON() ?? null,
      dateExpiration: offreEmploi.dateExpiration?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOffreEmploi: RestOffreEmploi): IOffreEmploi {
    return {
      ...restOffreEmploi,
      datePublication: restOffreEmploi.datePublication ? dayjs(restOffreEmploi.datePublication) : undefined,
      dateExpiration: restOffreEmploi.dateExpiration ? dayjs(restOffreEmploi.dateExpiration) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOffreEmploi>): HttpResponse<IOffreEmploi> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOffreEmploi[]>): HttpResponse<IOffreEmploi[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
