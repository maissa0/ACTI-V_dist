import { TestBed } from '@angular/core/testing';

import { TypeRessourceServiceService } from './type-ressource-service.service';

describe('TypeRessourceServiceService', () => {
  let service: TypeRessourceServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TypeRessourceServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
