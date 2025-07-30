import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CVResolve from './route/cv-routing-resolve.service';

const cVRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cv.component').then(m => m.CVComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cv-detail.component').then(m => m.CVDetailComponent),
    resolve: {
      cV: CVResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cv-update.component').then(m => m.CVUpdateComponent),
    resolve: {
      cV: CVResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cv-update.component').then(m => m.CVUpdateComponent),
    resolve: {
      cV: CVResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cVRoute;
