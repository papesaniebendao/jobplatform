import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICandidature, NewCandidature } from '../candidature.model';

export type PartialUpdateCandidature = Partial<ICandidature> & Pick<ICandidature, 'id'>;

type RestOf<T extends ICandidature | NewCandidature> = Omit<T, 'datePostulation'> & {
  datePostulation?: string | null;
};

export type RestCandidature = RestOf<ICandidature>;

export type NewRestCandidature = RestOf<NewCandidature>;

export type PartialUpdateRestCandidature = RestOf<PartialUpdateCandidature>;

export type EntityResponseType = HttpResponse<ICandidature>;
export type EntityArrayResponseType = HttpResponse<ICandidature[]>;

@Injectable({ providedIn: 'root' })
export class CandidatureService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/candidatures');

  create(candidature: NewCandidature): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(candidature);
    return this.http
      .post<RestCandidature>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(candidature: ICandidature): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(candidature);
    return this.http
      .put<RestCandidature>(`${this.resourceUrl}/${this.getCandidatureIdentifier(candidature)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(candidature: PartialUpdateCandidature): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(candidature);
    return this.http
      .patch<RestCandidature>(`${this.resourceUrl}/${this.getCandidatureIdentifier(candidature)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCandidature>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCandidature[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCandidatureIdentifier(candidature: Pick<ICandidature, 'id'>): number {
    return candidature.id;
  }

  compareCandidature(o1: Pick<ICandidature, 'id'> | null, o2: Pick<ICandidature, 'id'> | null): boolean {
    return o1 && o2 ? this.getCandidatureIdentifier(o1) === this.getCandidatureIdentifier(o2) : o1 === o2;
  }

  addCandidatureToCollectionIfMissing<Type extends Pick<ICandidature, 'id'>>(
    candidatureCollection: Type[],
    ...candidaturesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const candidatures: Type[] = candidaturesToCheck.filter(isPresent);
    if (candidatures.length > 0) {
      const candidatureCollectionIdentifiers = candidatureCollection.map(candidatureItem => this.getCandidatureIdentifier(candidatureItem));
      const candidaturesToAdd = candidatures.filter(candidatureItem => {
        const candidatureIdentifier = this.getCandidatureIdentifier(candidatureItem);
        if (candidatureCollectionIdentifiers.includes(candidatureIdentifier)) {
          return false;
        }
        candidatureCollectionIdentifiers.push(candidatureIdentifier);
        return true;
      });
      return [...candidaturesToAdd, ...candidatureCollection];
    }
    return candidatureCollection;
  }

  protected convertDateFromClient<T extends ICandidature | NewCandidature | PartialUpdateCandidature>(candidature: T): RestOf<T> {
    return {
      ...candidature,
      datePostulation: candidature.datePostulation?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCandidature: RestCandidature): ICandidature {
    return {
      ...restCandidature,
      datePostulation: restCandidature.datePostulation ? dayjs(restCandidature.datePostulation) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCandidature>): HttpResponse<ICandidature> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCandidature[]>): HttpResponse<ICandidature[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
