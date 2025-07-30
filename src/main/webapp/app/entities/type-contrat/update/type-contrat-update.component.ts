import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITypeContrat } from '../type-contrat.model';
import { TypeContratService } from '../service/type-contrat.service';
import { TypeContratFormGroup, TypeContratFormService } from './type-contrat-form.service';

@Component({
  selector: 'jhi-type-contrat-update',
  templateUrl: './type-contrat-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TypeContratUpdateComponent implements OnInit {
  isSaving = false;
  typeContrat: ITypeContrat | null = null;

  protected typeContratService = inject(TypeContratService);
  protected typeContratFormService = inject(TypeContratFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TypeContratFormGroup = this.typeContratFormService.createTypeContratFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typeContrat }) => {
      this.typeContrat = typeContrat;
      if (typeContrat) {
        this.updateForm(typeContrat);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const typeContrat = this.typeContratFormService.getTypeContrat(this.editForm);
    if (typeContrat.id !== null) {
      this.subscribeToSaveResponse(this.typeContratService.update(typeContrat));
    } else {
      this.subscribeToSaveResponse(this.typeContratService.create(typeContrat));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITypeContrat>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(typeContrat: ITypeContrat): void {
    this.typeContrat = typeContrat;
    this.typeContratFormService.resetForm(this.editForm, typeContrat);
  }
}
