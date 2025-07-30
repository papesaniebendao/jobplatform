import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICV } from '../cv.model';

@Component({
  selector: 'jhi-cv-detail',
  templateUrl: './cv-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CVDetailComponent {
  cV = input<ICV | null>(null);

  previousState(): void {
    window.history.back();
  }
}
