import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITypeContrat } from '../type-contrat.model';
import { TypeContratService } from '../service/type-contrat.service';

@Component({
  templateUrl: './type-contrat-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TypeContratDeleteDialogComponent {
  typeContrat?: ITypeContrat;

  protected typeContratService = inject(TypeContratService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.typeContratService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
