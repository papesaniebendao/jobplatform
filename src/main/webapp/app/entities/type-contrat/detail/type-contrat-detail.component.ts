import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ITypeContrat } from '../type-contrat.model';

@Component({
  selector: 'jhi-type-contrat-detail',
  templateUrl: './type-contrat-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class TypeContratDetailComponent {
  typeContrat = input<ITypeContrat | null>(null);

  previousState(): void {
    window.history.back();
  }
}
