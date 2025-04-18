import { TestBed } from '@angular/core/testing';

import { TypeRessourceService } from './type-ressource-service.service';

describe('TypeRessourceService', () => {
  let service: TypeRessourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TypeRessourceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
