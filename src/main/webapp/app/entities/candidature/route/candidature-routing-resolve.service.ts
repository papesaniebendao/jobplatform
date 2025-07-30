import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICandidature } from '../candidature.model';
import { CandidatureService } from '../service/candidature.service';

const candidatureResolve = (route: ActivatedRouteSnapshot): Observable<null | ICandidature> => {
  const id = route.params.id;
  if (id) {
    return inject(CandidatureService)
      .find(id)
      .pipe(
        mergeMap((candidature: HttpResponse<ICandidature>) => {
          if (candidature.body) {
            return of(candidature.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default candidatureResolve;
